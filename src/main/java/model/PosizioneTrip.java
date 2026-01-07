package model;

import com.google.transit.realtime.GtfsRealtime;

public class PosizioneTrip {

    private GtfsRealtime.Position position;
    private Trip trip;

    public PosizioneTrip(GtfsRealtime.Position position, Trip trip) {
        this.position = position;
        this.trip = trip;
    }

    public GtfsRealtime.Position getPosition() {
        return position;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void setPosition(GtfsRealtime.Position position) {
        this.position = position;
    }


}
