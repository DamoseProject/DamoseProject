package model; // O package util; a seconda di dove la metti

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RealTimeHandler {

    // URL ufficiale per i ritardi (Trip Updates)
    private static final String REALTIME_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";

    // Cache: TripID -> Ritardo in secondi
    private static Map<String, Integer> ritardiCache = new HashMap<>();

    private static long ultimoAggiornamento = 0;
    // Aggiorniamo ogni 30 secondi
    private static final long TEMPO_REFRESH_MS = 30 * 1000;

    /**
     * Ritorna il ritardo in secondi per un dato Trip ID.
     * Ritorna NULL se il bus non ha dati realtime (es. GPS spento o viaggio non iniziato).
     */
    public static Optional<Integer> getRitardo(String tripId) {
        aggiornaDatiSeNecessario();
        return Optional.ofNullable(ritardiCache.get(tripId));
    }

    private static void aggiornaDatiSeNecessario() {
        if (System.currentTimeMillis() - ultimoAggiornamento > TEMPO_REFRESH_MS) {
            scaricaDati();
        }
    }

    private static void scaricaDati() {
        System.out.println("[RealTime] Download dati in corso...");

        try (InputStream stream = new URL(REALTIME_URL).openStream()) {
            // La libreria legge il formato binario Protocol Buffer
            FeedMessage feed = FeedMessage.parseFrom(stream);

            Map<String, Integer> nuovaMappa = new HashMap<>();

            for (FeedEntity entity : feed.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    TripUpdate tripUpdate = entity.getTripUpdate();
                    String tripId = tripUpdate.getTrip().getTripId();

                    // Cerchiamo l'aggiornamento sulla fermata
                    if (tripUpdate.getStopTimeUpdateCount() > 0) {
                        StopTimeUpdate update = tripUpdate.getStopTimeUpdate(0);

                        // Il ritardo può essere su "Arrival" o "Departure"
                        if (update.hasArrival() && update.getArrival().hasDelay()) {
                            nuovaMappa.put(tripId, update.getArrival().getDelay());
                        } else if (update.hasDeparture() && update.getDeparture().hasDelay()) {
                            nuovaMappa.put(tripId, update.getDeparture().getDelay());
                        }
                    }
                }
            }

            ritardiCache = nuovaMappa;
            ultimoAggiornamento = System.currentTimeMillis();
            System.out.println("[RealTime] Aggiornato. Bus monitorati: " + ritardiCache.size());

        } catch (Exception e) {
            System.err.println("[RealTime] Errore download: " + e.getMessage());
        }
    }
}