package model;

import java.util.Objects;

public class Bus {

    private static int numberBus = 0;
    private String idBus;
    private String labelBus;
    private String licensePlate;
    private float latitude;
    private float longitude;


    public Bus(String id, String label, String licensePlate) {
        this.idBus = id;
        this.labelBus = label;
        this.licensePlate = licensePlate;

        numberBus++;
    }






    public boolean equals(Bus otherBus) {
        return Objects.equals(this.idBus, otherBus.idBus);
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }


    public static int getNumberBus() {
        return numberBus;
    }

    public String getIdBus() {
        return idBus;
    }

    public String getLabelBus() {
        return labelBus;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}
