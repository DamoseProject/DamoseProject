package model;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.transit.realtime.GtfsRealtime.VehicleDescriptor;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public class GtfsRealtimeExample {



    private static Database db;
    private static Connection connection;
    private static String LINK = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";





    public static void main(String[] args) throws Exception {

        db = new Database();
        db.connect();

        ArrayList<Bus> busList = db.getBusList();
        ArrayList<String> busIds = db.getIdBusList();
        ArrayList<Stop> stopsList = db.getStops();


        URL url = new URL(LINK);
        FeedMessage feed = FeedMessage.parseFrom(url.openStream());

        for (FeedEntity entity : feed.getEntityList()) {
            if (entity.hasTripUpdate()) {
                System.out.println(entity.getAllFields());
                System.exit(0);
                VehicleDescriptor vehicle = entity.getTripUpdate().getVehicle();

                if(entity.getTripUpdate().hasVehicle() && !busIds.contains(vehicle.getId())) {
                    busIds.add(vehicle.getId());
                    Bus bus = new Bus(vehicle.getId(), vehicle.getLabel(), vehicle.getLicensePlate());
                    busList.add(bus);
                    db.addBus(bus);
                    //System.out.println(vehicle.getId() + " " + vehicle.getLabel() + " " + vehicle.getLicensePlate());
                }

            }

        }

        System.out.println(busList.size());


    }
}