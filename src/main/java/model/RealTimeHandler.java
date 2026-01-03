package model;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RealTimeHandler {

    private static final String URL_REALTIME = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";

    // Usiamo ConcurrentHashMap per evitare crash se più thread accedono insieme
    private static final Map<String, Long> ritardiMap = new ConcurrentHashMap<>();

    private static long lastUpdate = 0;
    private static final long REFRESH_RATE_MS = 30 * 1000; // 30 Secondi

    /**
     * Scarica i dati solo se sono vecchi, altrimenti usa la cache.
     * Metodo sincronizzato per evitare download doppi.
     */
    public static synchronized void refreshData() {
        if (System.currentTimeMillis() - lastUpdate < REFRESH_RATE_MS) {
            return; // Dati ancora freschi
        }

        System.out.println("[RealTime] Scaricamento aggiornamenti...");
        try (InputStream stream = new URL(URL_REALTIME).openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(stream);

            // Creiamo una mappa temporanea per non bloccare la lettura durante il parse
            Map<String, Long> tempMap = new ConcurrentHashMap<>();

            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    TripUpdate tu = entity.getTripUpdate();
                    String tripId = tu.getTrip().getTripId();

                    // Cerchiamo il ritardo nell'ultimo StopTimeUpdate disponibile
                    // (Spesso il primo della lista è il prossimo evento)
                    if (tu.getStopTimeUpdateCount() > 0) {
                        TripUpdate.StopTimeUpdate stu = tu.getStopTimeUpdate(0);
                        long delay = 0;
                        if (stu.hasArrival() && stu.getArrival().hasDelay()) {
                            delay = stu.getArrival().getDelay();
                        } else if (stu.hasDeparture() && stu.getDeparture().hasDelay()) {
                            delay = stu.getDeparture().getDelay();
                        }
                        tempMap.put(tripId, delay);
                    }
                }
            }

            ritardiMap.clear();
            ritardiMap.putAll(tempMap);
            lastUpdate = System.currentTimeMillis();
            System.out.println("[RealTime] Aggiornato. Bus trovati: " + ritardiMap.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Arricchisce il record con i dati realtime se presenti
     */
    public static void applicaRealTime(BusInUnaFermataRecord bus) {
        refreshData(); // Controlla se serve aggiornare
        if (ritardiMap.containsKey(bus.getTripId())) {
            bus.setRitardoInSecondi(ritardiMap.get(bus.getTripId()));
        }
    }
}