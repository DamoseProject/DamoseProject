package Scraper;

import model.Database;
import model.StopTime;
import model.Trip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class Scrape_STOP_TIMES {
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("trip_id", "arrival_time", "departure_time", "stop_id", "stop_sequence", "stop_headsign", "pickup_type", "drop_off_type", "shape_dist_traveled", "timepoint"));
    final private static int indexArrivalTime = titles.indexOf("arrival_time");
    final private static int indexDepartureTime = titles.indexOf("departure_time");
    final private static int indexTripID = titles.indexOf("trip_id");
    final private static int indexStopID = titles.indexOf("stop_id");
    final private static int indexStopSequence = titles.indexOf("stop_sequence");
    final private static int indexStopHeadsign = titles.indexOf("stop_headsign");
    final private static int indexShapeDistTraveled = titles.indexOf("shape_dist_traveled");


    private static String filePath = "/home/carmine/Scaricati/rome_static_gtfs/stop_times.txt";

    public static void scrape() throws IOException, SQLException {
        Database db = new Database();
        db.connect();

        String contentFile = Files.readString(Path.of(filePath));
        String[] lines = contentFile.split(System.lineSeparator());


        for (String line : lines) {
            if (line.contains("trip_id")) {
                continue;
            }
            String[] words = line.split(",");
            String arrivalTime = words[indexArrivalTime];
            String departureTime = words[indexDepartureTime];
            String tripID = words[indexTripID];
            String stopID = words[indexStopID];
            String stopSequence = words[indexStopSequence];
            String stopHeadsign = words[indexStopHeadsign];
            String shapeDistTraveled = words[indexShapeDistTraveled];


            StopTime stopTime;
            try{
                stopTime = new StopTime(arrivalTime,departureTime,tripID,stopID,Integer.parseInt(stopSequence),stopHeadsign, Integer.parseInt(shapeDistTraveled));
            }
            catch(Exception e){

                /// /// CONTROLLARE ECCEZIONI

                stopTime = new StopTime(arrivalTime,departureTime,tripID,stopID,-1,stopHeadsign, -1);
            }
            db.addStopTime(stopTime);
        }


    }

    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }


}
