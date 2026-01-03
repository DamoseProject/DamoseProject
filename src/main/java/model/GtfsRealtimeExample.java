package model;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.sun.source.tree.YieldTree;
import util.TimeComparator;
import util.TimeManager;

public class GtfsRealtimeExample {



    private static Database db;
    private static Connection connection;
    private static String LINK = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";

    public GtfsRealtimeExample() throws SQLException {
    }


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

        Database db = new Database();

        System.exit(0);




















    }


    public static void updateBus(TripUpdate entity) throws SQLException {
        String tripId = entity.getTrip().getTripId();
        List<TripUpdate.StopTimeUpdate> aggiornamentiFermate = entity.getStopTimeUpdateList();
        int delay = entity.getDelay();
        String routeId = entity.getTrip().getRouteId();
        System.out.println(tripId + " " + aggiornamentiFermate.size() + " " + routeId);



        //Aggiornamenti fermate
        for(TripUpdate.StopTimeUpdate aggiornamentoFermata : aggiornamentiFermate){
            String nextStopId = aggiornamentoFermata.getStopId();

            TripUpdate.StopTimeEvent arrivo = aggiornamentoFermata.getArrival();
            long arrivoOra = arrivo.getTime();


            System.out.println(db.getStop(nextStopId).getName());
            System.out.println("Arrivo: " + TimeManager.getDate(arrivoOra, "hh:mm"));

        }
        System.exit(0);

    }







}