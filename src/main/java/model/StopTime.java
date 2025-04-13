package model;

public class StopTime {
    private String arrivalTime;
    private String departureTime;
    private String tripID;
    private String stopID;
    private int stopSequence;
    private String stopHeadsign;
    private int shapeDistTraveled;

    public StopTime(String arrivalTime, String departureTime, String tripID, String stopID, int stopSequence, String stopHeadsign, int shapeDistTraveled) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.tripID = tripID;
        this.stopID = stopID;
        this.stopSequence = stopSequence;
        this.stopHeadsign = stopHeadsign;
        this.shapeDistTraveled = shapeDistTraveled;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getTripID() {
        return tripID;
    }

    public String getStopID() {
        return stopID;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public int getShapeDistTraveled() {
        return shapeDistTraveled;
    }
}
