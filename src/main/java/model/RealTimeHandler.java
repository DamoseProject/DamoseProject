package model;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestore centralizzato dei dati in tempo reale (GTFS-Realtime).
 * Questa classe scarica e mantiene in cache le informazioni dinamiche riguardanti
 * i ritardi dei viaggi (Trip Updates), le posizioni geografiche dei mezzi (Vehicle Positions)
 * e lo stato di affollamento (Occupancy Status).
 * * <p>Implementa un meccanismo di aggiornamento automatico con una frequenza di refresh
 * controllata per ottimizzare le richieste di rete.</p>
 */
public class RealTimeHandler {

    private static final String URL_TRIP_UPDATES = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private static final String URL_VEHICLE_POSITIONS = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";

    /** Mappa dei ritardi correnti indicizzati per TripId */
    private static final Map<String, Long> ritardiMap = new ConcurrentHashMap<>();

    /** Mappa delle posizioni correnti dei bus indicizzate per TripId */
    private static final Map<String, GtfsRealtime.Position> posizioniMap = new ConcurrentHashMap<>();

    /** Mappa dello stato di affollamento indicizzata per TripId */
    private static final Map<String, VehiclePosition.OccupancyStatus> occupancyMap = new ConcurrentHashMap<>();

    private static long lastUpdate = 0;
    private static final long REFRESH_RATE_MS = 30 * 1000; // 30 Secondi

    /**
     * Coordina l'aggiornamento dei dati dai feed remoti.
     * Se l'ultimo aggiornamento è avvenuto meno di 30 secondi fa, il metodo termina
     * immediatamente per evitare spreco di banda e risorse.
     */
    public static synchronized void refreshData() {
        if (System.currentTimeMillis() - lastUpdate < REFRESH_RATE_MS) {
            return;
        }

        System.out.println("[RealTime] Inizio aggiornamento dati (Trip Updates + Vehicle Positions)...");

        updateTripUpdates();
        updateVehiclePositions();

        lastUpdate = System.currentTimeMillis();
        System.out.println("[RealTime] Aggiornamento completato. Ritardi: " + ritardiMap.size() +
                ", Posizioni: " + posizioniMap.size() +
                ", Affollamento: " + occupancyMap.size());
    }

    /**
     * Esegue il download e il parsing del feed relativo ai ritardi dei viaggi.
     */
    private static void updateTripUpdates() {
        try (InputStream stream = new URL(URL_TRIP_UPDATES).openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(stream);
            Map<String, Long> tempRitardi = new ConcurrentHashMap<>();

            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    TripUpdate tu = entity.getTripUpdate();
                    String tripId = tu.getTrip().getTripId();

                    if (tu.getStopTimeUpdateCount() > 0) {
                        TripUpdate.StopTimeUpdate stu = tu.getStopTimeUpdate(0);
                        long delay = 0;
                        if (stu.hasArrival() && stu.getArrival().hasDelay()) {
                            delay = stu.getArrival().getDelay();
                        } else if (stu.hasDeparture() && stu.getDeparture().hasDelay()) {
                            delay = stu.getDeparture().getDelay();
                        }
                        tempRitardi.put(tripId, delay);
                    }
                }
            }
            ritardiMap.clear();
            ritardiMap.putAll(tempRitardi);
        } catch (Exception e) {
            System.err.println("[RealTime] Errore nel download dei Trip Updates: " + e.getMessage());
        }
    }

    /**
     * Esegue il download e il parsing del feed relativo alle posizioni e all'affollamento dei bus.
     */
    private static void updateVehiclePositions() {
        try (InputStream stream = new URL(URL_VEHICLE_POSITIONS).openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(stream);
            Map<String, GtfsRealtime.Position> tempPosizioni = new ConcurrentHashMap<>();
            Map<String, VehiclePosition.OccupancyStatus> tempOccupancy = new ConcurrentHashMap<>();

            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasVehicle()) {
                    VehiclePosition vp = entity.getVehicle();

                    if (vp.hasTrip() && vp.getTrip().hasTripId()) {
                        String tripId = vp.getTrip().getTripId();

                        if (vp.hasPosition()) {
                            tempPosizioni.put(tripId, vp.getPosition());
                        }

                        if (vp.hasOccupancyStatus()) {
                            tempOccupancy.put(tripId, vp.getOccupancyStatus());
                        }
                    }
                }
            }
            posizioniMap.clear();
            posizioniMap.putAll(tempPosizioni);

            occupancyMap.clear();
            occupancyMap.putAll(tempOccupancy);

        } catch (Exception e) {
            System.err.println("[RealTime] Errore nel download delle Vehicle Positions: " + e.getMessage());
        }
    }

    /**
     * Arricchisce il record con i dati di ritardo e affollamento
     */
    public static void applicaRealTime(BusInUnaFermataRecord bus) {
        refreshData();

        if (ritardiMap.containsKey(bus.getTripId())) {
            bus.setRitardoInSecondi(ritardiMap.get(bus.getTripId()));
        }

        if (occupancyMap.containsKey(bus.getTripId())) {
            VehiclePosition.OccupancyStatus status = occupancyMap.get(bus.getTripId());
            bus.setAffollamento(traduciOccupancy(status));
        } else {
            bus.setAffollamento("DATO NON DISPONIBILE");
        }
    }

    /**
     * Traduce l'enum di GTFS in una stringa leggibile per l'utente
     */
    private static String traduciOccupancy(VehiclePosition.OccupancyStatus status) {
        switch (status) {
            case EMPTY: return "Posti: VUOTO";
            case MANY_SEATS_AVAILABLE: return "Posti: MOLTI DISPONIBILI";
            case FEW_SEATS_AVAILABLE: return "Posti: POCHI DISPONIBILI";
            case STANDING_ROOM_ONLY: return "Posti: SOLO IN PIEDI";
            case CRUSHED_STANDING_ROOM_ONLY: return "Posti: PIENO DA SCOPPIARE";
            case FULL: return "Posti: COMPLETO";
            case NOT_ACCEPTING_PASSENGERS: return "Posti: NON ACCETTA PASSEGGERI";
            case NOT_BOARDABLE: return "Posti: IMPOSSIBILE SALIRE";
            default: return "DATO NON DISPONIBILE";
        }
    }


    /**
     * Restituisce la posizione di un trip
     * @param trip
     */
    public static PosizioneTrip getPosizioneTrip(Trip trip) {
        refreshData();
        if (posizioniMap.containsKey(trip.getId())) {
            return new PosizioneTrip(posizioniMap.get(trip.getId()), trip);
        }
        return null;
    }
}