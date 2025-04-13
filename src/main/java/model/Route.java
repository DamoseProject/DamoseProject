package model;

public class Route {

    private static int routeCounter = 0;
    private String id;
    private String agencyCode;
    private String shortName;
    private String longName;
    private String type;


    public Route(String id, String agencyCode, String shortName, String longName, String type) {
        this.id = id;
        this.agencyCode = agencyCode;
        this.shortName = shortName;
        this.longName = longName;
        this.type = type;
        routeCounter++;
    }


    public String getId() {
        return id;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getType() {
        return type;
    }

    public static int getCount(){
        return routeCounter;
    }

}