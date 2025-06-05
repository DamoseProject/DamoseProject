package model;

public class Stop {

    private static int stopCounter = 0;
    private String id;
    private String code;
    private String name;
    private String latitude;
    private String longitude;

    public Stop(String id, String code, String name, String latitude, String longitude) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        stopCounter++;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static int getCount(){
        return stopCounter;
    }

    public String getLatitude() {return latitude;}

    public String getLongitude() {return longitude;}
}
