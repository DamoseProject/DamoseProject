package model;

public class BusInUnaFermataRecord {

    private String tripId;
    private String routeId;
    private String serviceId;
    private String textDestination;
    private String shortName;
    private int direction;
    private String arrivalTime;
    private String departureTime;


    public BusInUnaFermataRecord(String tripId, String routeId, String serviceId, String textDestination, String shortName, int direction, String arrivalTime, String departureTime) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.textDestination = textDestination;
        this.shortName = shortName;
        this.direction = direction;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTextDestination() {
        return textDestination;
    }

    public String getShortName() {
        return shortName;
    }

    public int getDirection() {
        return direction;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }
}
