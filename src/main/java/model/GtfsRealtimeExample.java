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
        db.connect();





        try {
            BusInUnaFermataRecord prossimoBus = db.getProssimoArrivoInUnaFermataDiUnaLineaRealTime("70120", "62"); // ID Fermata, ID Linea

            if (prossimoBus != null) {
                System.out.print("Prossimo bus alle: " + prossimoBus.getArrivalTime());

                if (prossimoBus.isRealTime()) {
                    long ritardo = prossimoBus.getRitardoInSecondi() / 60;
                    System.out.println(" (RITARDO LIVE: " + ritardo + " min)");
                } else {
                    System.out.println(" (Orario programmato)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }




        System.exit(0);








        BusInUnaFermataRecord prossimoArrivo = db.getProssimoArrivoInUnaLineaInUnaFermata("75466", "669");
        String tripId = prossimoArrivo.getTripId();


        System.out.println(prossimoArrivo.getRouteId() + " " + prossimoArrivo.getArrivalTime() + " " + prossimoArrivo.getTripId());



        FeedMessage feedMessage;

        try {
            URL url = new URL(LINK);
            feedMessage = FeedMessage.parseFrom(url.openStream());
        }
        catch(Exception e){
            throw new Exception("Errore nella lettura del feed");
        }

        int i = 0;
        boolean trovato = false;
        for(FeedEntity feedEntity : feedMessage.getEntityList()){
            if(feedEntity.hasTripUpdate()){
                TripUpdate tripUpdate = feedEntity.getTripUpdate();
                if(tripUpdate.getTrip().getTripId().equals(tripId)){
                    trovato = true;
                }
            }

        }
        System.out.println(trovato);


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




    static void toRun(Database db) throws Exception {
        //BusInUnaFermataRecord prossimoArrivo = db.getProssimoArrivoInUnaFermata("82136");

        BusInUnaFermataRecord prossimoArrivo = db.getProssimoArrivoInUnaLineaInUnaFermata("70067", "62");


        String tripId = prossimoArrivo.getTripId();
        String stopId = "70067";

        System.out.println("Linea " + prossimoArrivo.getRouteId() +
                " - Arrivo programmato: " + prossimoArrivo.getArrivalTime());

        FeedMessage feedMessage;

        try {
            URL url = new URL(LINK);
            feedMessage = FeedMessage.parseFrom(url.openStream());
        } catch(Exception e) {
            throw new Exception("Errore nella lettura del feed");
        }

        boolean trovato = false;

        for(FeedEntity feedEntity : feedMessage.getEntityList()) {
            if(feedEntity.hasTripUpdate()) {
                TripUpdate tripUpdate = feedEntity.getTripUpdate();

                //System.out.println(tripUpdate.getTrip().getTripId());


                // Verifica se è la corsa giusta
                if(tripUpdate.getTrip().getTripId().equals(tripId)) {
                    System.out.println("✓ Trovata corsa " + tripId);

                    // Cerca l'aggiornamento per la tua fermata specifica
                    for(TripUpdate.StopTimeUpdate stopTimeUpdate : tripUpdate.getStopTimeUpdateList()) {

                        if(stopTimeUpdate.getStopId().equals(stopId)) {
                            System.out.println("✓ Trovata fermata " + stopId);

                            // QUI c'è il ritardo!
                            if(stopTimeUpdate.hasArrival()) {
                                int delaySeconds = stopTimeUpdate.getArrival().getDelay();
                                int delayMinutes = delaySeconds / 60;

                                System.out.println("Ritardo: " + delaySeconds + " secondi (" +
                                        delayMinutes + " minuti)");

                                // Calcola l'orario stimato

                                String[] orario = prossimoArrivo.getArrivalTime().split(":");
                                int ora = Integer.valueOf(orario[0]);
                                int minuto = Integer.valueOf(orario[1]);
                                int secondo = Integer.valueOf(orario[2]);

                                LocalTime orarioStimato = LocalTime.of(ora, minuto, secondo).plusSeconds(delaySeconds);

                                System.out.println(orarioStimato.format(DateTimeFormatter.ofPattern("hh:mm:ss")));


                                trovato = true;
                                break;
                            }
                        }
                    }

                    if(trovato) break;
                }
            }
        }

        if(!trovato) {
            System.out.println("⚠ Nessun aggiornamento realtime disponibile - usa orario programmato");
        }
    }





}