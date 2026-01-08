package model;

import java.time.LocalTime;

public class BusInUnaFermataRecord implements Comparable<BusInUnaFermataRecord> {

    private final String tripId;
    private final String routeId;
    private String stopId;
     private final String serviceId;
    private final String textDestination;
    private final String shortName;
    private int direction;
    private final String arrivalTimeStr;
    private final String departureTimeStr;

    private boolean isRealTime = false;
    private boolean isSmartPredicted = false; // True se è una stima storica
    private long delayInSeconds = 0;

    public BusInUnaFermataRecord(String tripId, String routeId, String serviceId,
                                 String textDestination, String shortName,
                                 int direction, String arrivalTime, String departureTime) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.textDestination = textDestination;
        this.shortName = shortName;
        this.direction = direction;
        this.arrivalTimeStr = arrivalTime;
        this.departureTimeStr = departureTime;
    }


    /**
     * Restituisce l'orario originale programmato (Tabella).
     * Utile per UI: "10:30"
     */
    public LocalTime getOrarioStatico() {
        return parseGtfsTime(this.arrivalTimeStr);
    }

    /**
     * Calcola l'orario effettivo sommando il ritardo all'orario schedulato.
     * Utile per UI: "10:35" (se 5 min ritardo)
     */
    public LocalTime getOrarioEffettivo() {
        LocalTime baseTime = getOrarioStatico();
        return baseTime.plusSeconds(delayInSeconds);
    }


    /**
     * Imposta il ritardo.
     * NOTA: Non imposta automaticamente isRealTime a true, perché
     * potremmo impostare un ritardo basato sullo STORICO (Smart).
     */
    public void setRitardoInSecondi(long secondi) {
        this.delayInSeconds = secondi;
    }

    /**
     * Gestisce il formato GTFS che può superare le 24h (es. "25:30:00")
     */
    private LocalTime parseGtfsTime(String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int second = Integer.parseInt(parts[2]);

            // Gestione orari notturni dopo la mezzanotte (24, 25, 26...)
            while (hour >= 24) {
                hour -= 24;
            }
            return LocalTime.of(hour, minute, second);
        } catch (Exception e) {
            // Fallback in caso di errori di parsing strani
            return LocalTime.of(0, 0, 0);
        }
    }

    @Override
    public int compareTo(BusInUnaFermataRecord other) {
        // Ordiniamo in base all'orario in cui il bus arriverà VERAMENTE
        return this.getOrarioEffettivo().compareTo(other.getOrarioEffettivo());
    }

    public String getTripId() { return tripId; }
    public String getRouteId() { return routeId; }
    public String getTextDestination() { return textDestination; }
    public String getShortName() { return shortName; }
    public String getStopId() { return stopId; }
    public void setStopId(String stopId) { this.stopId = stopId; } // Setter aggiunto

    public long getDelayInSeconds() { return delayInSeconds; }

    public boolean isRealTime() { return isRealTime; }
    public void setRealTime(boolean realTime) { this.isRealTime = realTime; }

    public boolean getIsSmartPredicted(){ return isSmartPredicted; }
    public void setIsSmartPredicted(boolean isSmartPredicted) { this.isSmartPredicted = isSmartPredicted; }
}