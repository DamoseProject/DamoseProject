package Scraper;

import model.Database;
import model.Stop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scrape_STOP {

    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "stop_url", "wheelchair_boarding", "stop_timezone", "location_type", "parent_station"));
    final private static int indexStopID = titles.indexOf("stop_id") ;
    final private static int indexStopCode = titles.indexOf("stop_code") ;
    final private static int indexStopName = titles.indexOf("stop_name") ;
    final private static int indexStopLat = titles.indexOf("stop_lat") ;
    final private static int indexStopLon = titles.indexOf("stop_lon") ;


    private static String filePath = "C:\\Users\\micko\\Downloads\\stops.txt";

    public static void scrape() throws IOException, SQLException {
        Database db = new Database();
        db.connect();

        //String contentFile = Files.readString(Path.of(filePath));
        //String[] lines = contentFile.split(System.lineSeparator());

        List<String> linesList = Files.readAllLines(Path.of(filePath));
        String[] lines = linesList.toArray(new String[0]);




        System.out.println("START SCRAPING");
        System.out.println(lines[0]);
        for(String line : lines) {
            try {
                String[] words = line.split(",");
                String stop_id = words[indexStopID];
                String stop_code = words[indexStopCode];
                String stop_name = words[indexStopName];

                System.out.println(words[indexStopLat]);

                float stop_lat = Float.parseFloat(words[indexStopLat]);
                float stop_lon = Float.parseFloat(words[indexStopLon]);
                Stop fermata = new Stop(stop_id, stop_code, stop_name, stop_lat, stop_lon);
                db.addStop(fermata);
            } catch (NumberFormatException ignored){

            }
        }
        System.out.println(Stop.getCount());


    }


    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }


}
