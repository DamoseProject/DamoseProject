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

public class DataCollector {

    private static final String URL_TRIP_UPDATES = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private final Database db;

    // Cache per ricordare cosa abbiamo già salvato in questa sessione
    // Formato stringa: "TRIP_ID#STOP_ID"
    private final Set<String> datiGiaSalvati = new HashSet<>();

    public DataCollector(Database db) {
        this.db = db;
    }

    /**
     * Avvia il ciclo infinito di raccolta dati.
     * Blocca il thread corrente, quindi eseguilo in un main separato.
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

            // Attende 60 secondi prima del prossimo giro
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void processaTripUpdate(TripUpdate tu) throws SQLException {
        String tripId = tu.getTrip().getTripId();
        String routeId = tu.getTrip().getRouteId();

        // Se il feed non contiene la RouteID, la cerchiamo nel DB (opzionale, ma più sicuro)
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

            // Calcoliamo il ritardo (Arrival o Departure)
            long delay = 0;
            if (stu.hasArrival() && stu.getArrival().hasDelay()) {
                delay = stu.getArrival().getDelay();
            } else if (stu.hasDeparture() && stu.getDeparture().hasDelay()) {
                delay = stu.getDeparture().getDelay();
            }

            // Logica: Salviamo solo se il ritardo è significativo o se vogliamo tutto
            // Qui salviamo tutto per avere una statistica completa
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
     * MAIN DI ESECUZIONE
     * Esegui questo metodo (tasto destro -> Run) per popolare il DB.
     */
    public static void main(String[] args) {
        Database db = new Database();
        db.connect(); // Assicurati che il file .db sia raggiungibile

        DataCollector collector = new DataCollector(db);
        collector.avviaRaccoltaDati();
    }
}