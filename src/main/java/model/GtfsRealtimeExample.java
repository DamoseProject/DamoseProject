package model;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.sun.source.tree.YieldTree;
import util.TimeComparator;
import util.TimeManager;

/**
 * Classe di utilità e test per l'elaborazione dei feed GTFS Realtime.
 * Fornisce esempi di come leggere gli aggiornamenti dei viaggi (TripUpdate) e
 * come stampare le informazioni sui ritardi e sulle fermate coinvolte,
 * interfacciandosi direttamente con il database per la risoluzione dei nomi.
 */
public class GtfsRealtimeExample {



    private static Database db;
    private static Connection connection;

    /** URL del feed binario (Protocol Buffer) per gli aggiornamenti dei viaggi di Roma Mobilità */
    private static String LINK = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";

    public GtfsRealtimeExample() throws SQLException {
    }

    /**
     * Estrae e stampa i dettagli di un singolo aggiornamento orario presso una fermata.
     * Mostra l'orario previsto, il ritardo in secondi e recupera il nome della fermata
     * dal database partendo dall'ID ricevuto nel feed.
     * * @param stopTimeUpdate L'oggetto contenente i dati di transito in tempo reale.
     * @throws SQLException Se si verifica un errore durante il recupero del nome della fermata.
     */
    public static void printUpdate(TripUpdate.StopTimeUpdate stopTimeUpdate) throws SQLException {

        long oraArrivo  = stopTimeUpdate.getArrival().getTime();
        int delay = stopTimeUpdate.getArrival().getDelay();
        int uncertainty = stopTimeUpdate.getArrival().getUncertainty();

        String oraArrivoStr = TimeManager.getDate(oraArrivo, "hh:mm");
        String stopID = stopTimeUpdate.getStopId();
        TripUpdate.StopTimeUpdate.ScheduleRelationship schedule = stopTimeUpdate.getScheduleRelationship();
        System.out.println(oraArrivoStr + " Fermata: " + db.getStop(stopID).getName());


    }



    /**
     * Metodo principale (Entry Point) per il test delle funzionalità di ricerca.
     * Esegue una connessione al database e testa il recupero delle fermate per una
     * specifica linea (es. "716") e la ricerca testuale (es. "Termini").
     */
    public static void main(String[] args) throws Exception {

        Database db = new Database();
        db.connect();

        List<Stop> stopsss = db.getStopsByRouteByDirection("716", 0);
        for(Stop stop : stopsss){
            System.out.println(stop.getName());
        }




        List<Stop> stops = db.getStopsByName("Termini");






        System.exit(0);




















    }

    /**
     * Estrae informazioni generali sul ritardo di un intero viaggio.
     * @param entity L'entità TripUpdate che rappresenta lo stato attuale di un bus in corsa.
     * @throws SQLException In caso di errore nel database.
     */
    public static void updateBus(TripUpdate entity) throws SQLException {
        String tripId = entity.getTrip().getTripId();
        List<TripUpdate.StopTimeUpdate> aggiornamentiFermate = entity.getStopTimeUpdateList();
        int delay = entity.getDelay();
        String routeId = entity.getTrip().getRouteId();
        System.out.println(tripId + " " + aggiornamentiFermate.size() + " " + routeId);




    }







}