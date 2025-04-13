package model;

public class Trip {
    private static int tripCount = 0;
    private String id;
    private String routeId;
    private String serviceId;
    private String tripHeadsign;
    private String tripShortName;
    private int direction;
    private int wheelchair_accessible;


    public Trip(String id, String routeId, String serviceId, String tripHeadsign, String tripShortName, int direction, int wheelchair_accessible) {
        this.id = id;
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.tripHeadsign = tripHeadsign;
        this.tripShortName = tripShortName;
        this.direction = direction;
        this.wheelchair_accessible = wheelchair_accessible;
        tripCount++;
    }


    public String getId() {
        return id;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public int getDirection() {
        return direction;
    }

    public int getWheelchair_accessible() {
        return wheelchair_accessible;
    }


    public static int getCount() {
        return tripCount;
    }

}
