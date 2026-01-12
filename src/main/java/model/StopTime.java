package model;

/**
 * Questa classe rappresenta il transito di un bus presso una fermata in un orario specifico.
 * Mappa la tabella "stop_times" dello standard GTFS, mettendo in relazione un viaggio
 * con una fermata e definendo l'ordine cronologico delle tappe lungo il percorso.
 */
public class StopTime {
    private String arrivalTime;
    private String departureTime;
    private String tripID;
    private String stopID;
    private int stopSequence;
    private String stopHeadsign;
    private int shapeDistTraveled;

    /**
     * Costruttore: inizializza un record di transito con orari e riferimenti.
     * * @param arrivalTime Orario di arrivo programmato (formato HH:mm:ss).
     * @param departureTime Orario di partenza programmato (formato HH:mm:ss).
     * @param tripID Identificativo univoco del viaggio (Trip).
     * @param stopID Identificativo univoco della fermata (Stop).
     * @param stopSequence Ordine progressivo della fermata all'interno del viaggio.
     * @param stopHeadsign Testo della destinazione visualizzato alla fermata.
     * @param shapeDistTraveled Distanza progressiva percorsa lungo il tragitto.
     */
    public StopTime(String arrivalTime, String departureTime, String tripID, String stopID, int stopSequence, String stopHeadsign, int shapeDistTraveled) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.tripID = tripID;
        this.stopID = stopID;
        this.stopSequence = stopSequence;
        this.stopHeadsign = stopHeadsign;
        this.shapeDistTraveled = shapeDistTraveled;
    }

    /** @return L'orario di arrivo programmato. */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /** @return L'orario di partenza programmato. */
    public String getDepartureTime() {
        return departureTime;
    }

    /** @return L'ID del viaggio a cui appartiene questo transito. */
    public String getTripID() {
        return tripID;
    }

    /** @return L'ID della fermata di transito. */
    public String getStopID() {
        return stopID;
    }

    /** @return La posizione progressiva della fermata nella sequenza del viaggio. */
    public int getStopSequence() {
        return stopSequence;
    }

    /** @return La direzione o destinazione indicata alla fermata. */
    public String getStopHeadsign() {
        return stopHeadsign;
    }

    /** @return La distanza totale percorsa dall'inizio del viaggio fino a questa fermata. */
    public int getShapeDistTraveled() {
        return shapeDistTraveled;
    }
}
