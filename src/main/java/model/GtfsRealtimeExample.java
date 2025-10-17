package model;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.sun.source.tree.YieldTree;
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



        List<Stop> stops = db.getStopsByName("Bologna");
        Stop bologna = stops.get(0);
        User me = db.getUserByEmail("mickolsverde06@outlook.it");


        List<Stop> fermatepreferite = db.getFavouriteStopsByUser(me);
        for(Stop fermata : fermatepreferite){
            System.out.println(fermata.getLongitude() + " " + fermata.getLatitude() + " " + fermata.getName() + " " + fermata.getId());
        }

        System.exit(0);



        FeedMessage feedMessage;

        try {
            URL url = new URL(LINK);
            feedMessage = FeedMessage.parseFrom(url.openStream());
        }
        catch(Exception e){
            throw new Exception("Errore nella lettura del feed");
        }

        for(FeedEntity feedEntity : feedMessage.getEntityList()){
            if(feedEntity.hasTripUpdate()){
                updateBus(feedEntity.getTripUpdate());
            }

        }


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