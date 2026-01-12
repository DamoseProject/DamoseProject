package model;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;

import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Questa classe si occupa della raccolta sistematica dei dati sulle performance del trasporto pubblico.
 * Funziona come un processo in background che scarica periodicamente i feed GTFS-Realtime,
 * estrae i ritardi dei bus e li salva nel database per alimentare le statistiche storiche.
 * * <p>Utilizza un set per evitare la duplicazione dei record durante la stessa sessione di raccolta.</p>
 */
public class DataCollector {

    /** URL del feed Protocol Buffer fornito dall'agenzia del trasporto pubblico */
    private static final String URL_TRIP_UPDATES = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private final Database db;

    /** Cache in-memory per evitare di salvare lo stesso transito più volte nello stesso ciclo di esecuzione */
    private final Set<String> datiGiaSalvati = new HashSet<>();

    /**
     * Costruttore: inizializza il collector associandolo a un database specifico.
     * @param db L'istanza del database dove salvare le osservazioni raccolte.
     */
    public DataCollector(Database db) {
        this.db = db;
    }

    /**
     * Avvia il ciclo infinito di raccolta dati.
     */
    public void avviaRaccoltaDati() {
        System.out.println("=== DATA COLLECTOR AVVIATO ===");
        System.out.println("Premere STOP o chiudere il programma per terminare.");

        while (true) {
            System.out.println("\n[COLLECTOR] Scaricamento feed...");
            long inizio = System.currentTimeMillis();
            int nuoviRecord = 0;

            try (InputStream stream = new URL(URL_TRIP_UPDATES).openStream()) {
                FeedMessage feed = FeedMessage.parseFrom(stream);

                for (FeedEntity entity : feed.getEntityList()) {
                    if (entity.hasTripUpdate()) {
                        processaTripUpdate(entity.getTripUpdate());
                    }
                }

            } catch (Exception e) {
                System.err.println("[COLLECTOR] Errore download feed: " + e.getMessage());
            }

            long fine = System.currentTimeMillis();
            System.out.println("[COLLECTOR] Ciclo finito in " + (fine - inizio) + "ms.");

            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Analizza un singolo aggiornamento di viaggio (TripUpdate).
     * Estrae il ritardo rilevato (arrival o departure delay) per ogni fermata del percorso
     * e invoca il database per il salvataggio storico.
     * * @param tu L'oggetto TripUpdate ricevuto dal feed.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     */
    private void processaTripUpdate(TripUpdate tu) throws SQLException {
        String tripId = tu.getTrip().getTripId();
        String routeId = tu.getTrip().getRouteId();

        if (routeId == null || routeId.isEmpty()) {
            try {
                routeId = db.getTrip(tripId).getRouteId();
            }
            catch (SQLException ignored) {
                return;
            }

            if(routeId == null || routeId.isEmpty()) return;


        }

        for (TripUpdate.StopTimeUpdate stu : tu.getStopTimeUpdateList()) {
            String stopId = stu.getStopId();

            String chiaveUnivoca = tripId + "#" + stopId;

            // Se abbiamo già salvato questo specifico bus in questa specifica fermata, SALTIAMO
            if (datiGiaSalvati.contains(chiaveUnivoca)) {
                continue;
            }

            long delay = 0;
            if (stu.hasArrival() && stu.getArrival().hasDelay()) {
                delay = stu.getArrival().getDelay();
            } else if (stu.hasDeparture() && stu.getDeparture().hasDelay()) {
                delay = stu.getDeparture().getDelay();
            }


            try {
                db.salvaOsservazioneStorica(routeId, stopId, (int) delay, false);

                // Aggiungiamo alla cache per non salvarlo di nuovo al prossimo minuto
                datiGiaSalvati.add(chiaveUnivoca);

            } catch (SQLException e) {
                System.err.println("Errore salvataggio DB: " + e.getMessage());
            }
        }
    }

    /**
     * Metodo entry-point per avviare il collector come processo indipendente.
     */
    public static void main(String[] args) {
        Database db = new Database();
        db.connect();

        DataCollector collector = new DataCollector(db);
        collector.avviaRaccoltaDati();
    }
}