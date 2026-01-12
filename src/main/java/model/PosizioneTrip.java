package model;

import com.google.transit.realtime.GtfsRealtime;

/**
 * Questa classe rappresenta la posizione geografica in tempo reale di uno specifico viaggio.
 * Mette in relazione un oggetto {@link Trip} (il viaggio programmato) con l'oggetto
 * {@link com.google.transit.realtime.GtfsRealtime.Position} (latitudine, longitudine e altre
 * info GPS) ricevuto tramite i feed Real-Time.
 */
public class PosizioneTrip {

    /** Dati geografici grezzi (Latitudine, Longitudine, Bearing) dal feed GTFS-Realtime */
    private GtfsRealtime.Position position;

    /** Riferimento al viaggio a cui appartiene questa posizione */
    private Trip trip;

    /**
     * Costruttore: crea un'associazione tra una posizione GPS e un viaggio.
     * * @param position L'oggetto Position contenente le coordinate attuali.
     * @param trip L'oggetto Trip che identifica la corsa del bus.
     */
    public PosizioneTrip(GtfsRealtime.Position position, Trip trip) {
        this.position = position;
        this.trip = trip;
    }

    /**
     * Restituisce i dati GPS della posizione.
     * @return Oggetto Position di GtfsRealtime.
     */
    public GtfsRealtime.Position getPosition() {
        return position;
    }

    /**
     * Restituisce il viaggio associato a questa posizione.
     * @return Oggetto Trip di riferimento.
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * Aggiorna il riferimento al viaggio.
     * @param trip Il nuovo oggetto Trip.
     */
    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    /**
     * Aggiorna la posizione geografica con nuovi dati GPS.
     * @param position Il nuovo oggetto Position.
     */
    public void setPosition(GtfsRealtime.Position position) {
        this.position = position;
    }


}
