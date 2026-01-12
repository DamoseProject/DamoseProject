package model;

import java.time.LocalTime;

/**
 * Questa classe rappresenta un record dettagliato di un autobus in transito presso una specifica fermata.
 * Funge da contenitore per le informazioni provenienti sia dai dati statici (GTFS) che da quelli
 * in tempo reale (GTFS-Realtime), permettendo di calcolare l'orario di arrivo previsto
 * considerando l'eventuale ritardo.
 * * Implementa {@link Comparable} per permettere l'ordinamento cronologico dei bus in arrivo.
 */
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

    private String affollamento = "DATO NON DISPONIBILE";

    private boolean isRealTime = false;
    private boolean isSmartPredicted = false; // True se è una stima storica
    private long delayInSeconds = 0;

    /**
     * Costruttore completo per inizializzare un record di transito.
     * @param tripId Identificativo del viaggio.
     * @param routeId Identificativo della linea.
     * @param serviceId Identificativo del servizio (es. feriale/festivo).
     * @param textDestination Destinazione finale visualizzata.
     * @param shortName Nome breve della linea (es. "64").
     * @param direction Direzione del viaggio (0 o 1).
     * @param arrivalTime Stringa orario di arrivo (formato HH:mm:ss).
     * @param departureTime Stringa orario di partenza (formato HH:mm:ss).
     */
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
     * Restituisce l'orario originale programmato (dati statici).
     */
    public LocalTime getOrarioStatico() {
        return parseGtfsTime(this.arrivalTimeStr);
    }

    /**
     * Calcola l'orario effettivo sommando il ritardo all'orario schedulato.
     */
    public LocalTime getOrarioEffettivo() {
        LocalTime baseTime = getOrarioStatico();
        return baseTime.plusSeconds(delayInSeconds);
    }


    /**
     * Imposta il ritardo.
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

            // Gestione orari notturni dopo la mezzanotte
            while (hour >= 24) {
                hour -= 24;
            }
            return LocalTime.of(hour, minute, second);
        } catch (Exception e) {
            return LocalTime.of(0, 0, 0);
        }
    }

    /**
     * Compara questo record con un altro basandosi sull'orario effettivo di arrivo.
     * Utilizzato per mostrare i bus in ordine di arrivo imminente.
     */
    @Override
    public int compareTo(BusInUnaFermataRecord other) {
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

    public String getAffollamento() {return affollamento;}
    public void setAffollamento(String affollamento) {this.affollamento = affollamento;}
}