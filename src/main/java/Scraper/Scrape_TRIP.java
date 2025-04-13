package Scraper;

import model.Database;
import model.Stop;
import model.Trip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Scrape_TRIP {
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("route_id","service_id","trip_id","trip_headsign","trip_short_name","direction_id","block_id","shape_id","wheelchair_accessible","exceptional"));
    final private static int indexRouteID = titles.indexOf("route_id");
    final private static int indexServiceID = titles.indexOf("service_id");
    final private static int indexTripID = titles.indexOf("trip_id");
    final private static int indexTripHeadsign = titles.indexOf("trip_headsign");
    final private static int indexTripShortName = titles.indexOf("trip_short_name");
    final private static int indexDirectionID = titles.indexOf("direction_id");
    final private static int indexWheelchairAccessible = titles.indexOf("wheelchair_accessible");



    private static String filePath = "/home/carmine/Scaricati/rome_static_gtfs/trips.txt";

    public static void scrape() throws IOException, SQLException {
        Database db = new Database();
        db.connect();

        String contentFile = Files.readString(Path.of(filePath));
        String[] lines = contentFile.split(System.lineSeparator());

        int counter = 0;

        for(String line : lines) {
            if(counter <= 162542) {
                counter++;
                continue;
            }
            String[] words = line.split(",");
            String routeID = words[indexRouteID];
            String serviceID = words[indexServiceID];
            String tripID = words[indexTripID];
            String tripHeadsign = words[indexTripHeadsign];
            String tripShortName = words[indexTripShortName];
            String directionID = words[indexDirectionID];
            String wheelchairAccessible = words[indexWheelchairAccessible];
            Trip trip;
            try{
                 trip = new Trip(tripID, routeID, serviceID, tripHeadsign, tripShortName, Integer.parseInt(directionID), Integer.parseInt(wheelchairAccessible));

            }
            catch(Exception e){
                /// / controllare eccezioni
                trip = new Trip(tripID, routeID, serviceID, tripHeadsign, tripShortName, -1, -1);
            }
            db.addTrip(trip);
            System.out.println(Trip.getCount());

        }



    }

    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }





}
