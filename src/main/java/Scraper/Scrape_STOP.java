package Scraper;

import model.Database;
import model.Stop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Scrape_STOP {

    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "stop_url", "wheelchair_boarding", "stop_timezone", "location_type", "parent_station"));
    final private static int indexStopID = titles.indexOf("stop_id") ;
    final private static int indexStopCode = titles.indexOf("stop_code") ;
    final private static int indexStopName = titles.indexOf("stop_name") ;
    final private static int indexStoplat = titles.indexOf("stop_lat") ;
    final private static int indexLon = titles.indexOf("stop_lon") ;


    private static String filePath = "/home/carmine/Scaricati/rome_static_gtfs/stops.txt";

    public static void scrape() throws IOException, SQLException {
        Database db = new Database();
        db.connect();

        String contentFile = Files.readString(Path.of(filePath));
        String[] lines = contentFile.split(System.lineSeparator());
        for(String line : lines) {
            String[] words = line.split(",");
            String stop_id = words[indexStopID];
            String stop_code = words[indexStopCode];
            String stop_name = words[indexStopName];
            String stop_lat = words[indexStoplat];
            String stop_lon = words[indexLon];

            Stop fermata = new Stop(stop_id, stop_code, stop_name, stop_lat, stop_lon);
            db.addStop(fermata);

        }
        System.out.println(Stop.getCount());


    }


    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }


}
