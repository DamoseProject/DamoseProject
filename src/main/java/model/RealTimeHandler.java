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

public class RealTimeHandler {

    private static final String URL_TRIP_UPDATES = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private static final String URL_VEHICLE_POSITIONS = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";

    private static final Map<String, Long> ritardiMap = new ConcurrentHashMap<>();
    private static final Map<String, GtfsRealtime.Position> posizioniMap = new ConcurrentHashMap<>();

    private static long lastUpdate = 0;
    private static final long REFRESH_RATE_MS = 30 * 1000; // 30 Secondi

    /**
     * Scarica i dati da entrambi i feed (Ritardi e Posizioni)
     */
    public static synchronized void refreshData() {
        if (System.currentTimeMillis() - lastUpdate < REFRESH_RATE_MS) {
            return;
        }

        System.out.println("[RealTime] Inizio aggiornamento dati (Trip Updates + Vehicle Positions)...");

        // 1. Aggiorna i Ritardi (Trip Updates)
        updateTripUpdates();

        // 2. Aggiorna le Posizioni (Vehicle Positions)
        updateVehiclePositions();

        lastUpdate = System.currentTimeMillis();
        System.out.println("[RealTime] Aggiornamento completato. Ritardi: " + ritardiMap.size() + ", Posizioni: " + posizioniMap.size());
    }

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

    private static void updateVehiclePositions() {
        try (InputStream stream = new URL(URL_VEHICLE_POSITIONS).openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(stream);
            Map<String, GtfsRealtime.Position> tempPosizioni = new ConcurrentHashMap<>();

            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasVehicle()) {
                    VehiclePosition vp = entity.getVehicle();
                    // Usiamo il tripId come chiave per far combaciare i dati con i ritardi
                    if (vp.hasTrip() && vp.getTrip().hasTripId()) {
                        String tripId = vp.getTrip().getTripId();
                        if (vp.hasPosition()) {
                            tempPosizioni.put(tripId, vp.getPosition());
                        }
                    }
                }
            }
            posizioniMap.clear();
            posizioniMap.putAll(tempPosizioni);
        } catch (Exception e) {
            System.err.println("[RealTime] Errore nel download delle Vehicle Positions: " + e.getMessage());
        }
    }

    /**
     * Arricchisce il record con i dati di ritardo
     */
    public static void applicaRealTime(BusInUnaFermataRecord bus) {
        refreshData();
        if (ritardiMap.containsKey(bus.getTripId())) {
            bus.setRitardoInSecondi(ritardiMap.get(bus.getTripId()));
        }
    }

    /**
     * Restituisce la posizione GPS per un determinato Trip
     */
    public static PosizioneTrip getPosizioneTrip(Trip trip) {
        refreshData();
        if (posizioniMap.containsKey(trip.getId())) {
            return new PosizioneTrip(posizioniMap.get(trip.getId()), trip);
        }
        return null;
    }
}