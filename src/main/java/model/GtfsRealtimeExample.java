package model;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import util.TimeManager;

public class GtfsRealtimeExample {



    private static Database db;
    private static Connection connection;
    private static String LINK = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";






    public static void printUpdate(TripUpdate.StopTimeUpdate stopTimeUpdate) throws SQLException {

        long oraArrivo  = stopTimeUpdate.getArrival().getTime();
        int delay = stopTimeUpdate.getArrival().getDelay();
        int uncertainty = stopTimeUpdate.getArrival().getUncertainty();

        String oraArrivoStr = TimeManager.getDate(oraArrivo, "hh:mm");
        String stopID = stopTimeUpdate.getStopId();
        TripUpdate.StopTimeUpdate.ScheduleRelationship schedule = stopTimeUpdate.getScheduleRelationship();
        System.out.println(oraArrivoStr + " Fermata: " + db.getStop(stopID).getName());


    }




    public static void main(String[] args) throws Exception {

        db = new Database();
        db.connect();
//
//        ArrayList<Bus> busList = db.getBusList();
//        ArrayList<String> busIds = db.getIdBusList();
//        ArrayList<Stop> stopsList = db.getStops();


        URL url = new URL(LINK);
        FeedMessage feed = FeedMessage.parseFrom(url.openStream());

        for (FeedEntity entity : feed.getEntityList()) {
            if (entity.hasTripUpdate()) {
                TripUpdate entityData = entity.getTripUpdate();
                String tripID = entityData.getTrip().getTripId();
                String routeID = entityData.getTrip().getRouteId();
                String startTime = entityData.getTrip().getStartDate();
                String startDate = entityData.getTrip().getStartDate();
                int directionID = entityData.getTrip().getDirectionId();

                //List<TripUpdate.StopTimeUpdate> stopTimeUpdates = entityData.getStopTimeUpdateList();


                System.out.println("AUTOBUS " + db.getRoute(routeID).getShortName());

                TripUpdate.StopTimeUpdate lastUpdate = entityData.getStopTimeUpdate(entityData.getStopTimeUpdateCount() - 1);
                printUpdate(lastUpdate);


                System.out.println("\n\n");

                //System.out.println(entity.getAllFields());
                System.out.println(tripID);
                System.exit(0);

            }

        }


    }
}