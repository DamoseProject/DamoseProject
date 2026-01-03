package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BusInUnaFermataRecord implements Comparable<BusInUnaFermataRecord> {

    private String tripId;
    private String routeId;
    private String serviceId;
    private String textDestination;
    private String shortName;
    private int direction;
    private String arrivalTimeStr; // Manteniamo la stringa originale DB
    private String departureTimeStr;

    private boolean isRealTime = false;
    private long delayInSeconds = 0;

    // Cache per evitare di ricalcolare ogni volta
    private LocalTime cachedEffectiveTime = null;

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

    // --- LOGICA CORE ---

    /**
     * Calcola l'orario effettivo sommando il ritardo all'orario schedulato.
     */
    public LocalTime getOrarioEffettivo() {
        // Parsiamo l'orario stringa (es. "10:30:00") in LocalTime
        LocalTime baseTime = parseGtfsTime(this.arrivalTimeStr);

        // Aggiungiamo il ritardo (può essere negativo se in anticipo)
        return baseTime.plusSeconds(delayInSeconds);
    }

    public void setRitardoInSecondi(long secondi) {
        this.delayInSeconds = secondi;
        this.isRealTime = true;
        // Resetta la cache se cambia il ritardo
        this.cachedEffectiveTime = null;
    }

    /**
     * Converte stringhe GTFS (es. "25:00:00") in LocalTime Java (es. 01:00:00).
     */
    private LocalTime parseGtfsTime(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2]);

        // Gestione orari oltre le 24h (GTFS standard)
        while (hour >= 24) {
            hour -= 24;
        }
        return LocalTime.of(hour, minute, second);
    }

    // --- INTERFACCIA COMPARABLE ---
    // Questo permette di ordinare le liste automaticamente con Collections.sort()
    @Override
    public int compareTo(BusInUnaFermataRecord other) {
        return this.getOrarioEffettivo().compareTo(other.getOrarioEffettivo());
    }

    // --- GETTER & SETTER ---
    public String getTripId() { return tripId; }
    public String getRouteId() { return routeId; }
    public String getTextDestination() { return textDestination; }
    public String getShortName() { return shortName; }

    // Ritorna l'orario originale schedulato (Stringa)
    public String getScheduledArrivalTime() { return arrivalTimeStr; }

    public long getDelayInSeconds() { return delayInSeconds; }
    public boolean isRealTime() { return isRealTime; }

    public void setRealTime(boolean realTime) { this.isRealTime = realTime; }
}