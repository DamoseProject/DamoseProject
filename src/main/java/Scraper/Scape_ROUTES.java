package Scraper;

import model.Database;
import model.Route;
import model.Stop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Scape_ROUTES {

    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("route_id", "agency_id","route_short_name","route_long_name","route_type","route_url","route_color","route_text_color"));
    final private static int indexRouteID = titles.indexOf("route_id") ;
    final private static int indexAgencyCode = titles.indexOf("agency_id") ;
    final private static int indexShortName = titles.indexOf("route_short_name") ;
    final private static int indexLongName = titles.indexOf("route_long_name") ;
    final private static int indexType = titles.indexOf("route_type") ;


    private static String filePath = "/home/carmine/Scaricati/rome_static_gtfs/routes.txt";

    public static void scrape() throws IOException, SQLException {
        Database db = new Database();
        db.connect();

        String contentFile = Files.readString(Path.of(filePath));
        String[] lines = contentFile.split(System.lineSeparator());
        for(String line : lines) {
            String[] words = line.split(",");
            String routeID = words[indexRouteID];
            String agencyCode = words[indexAgencyCode];
            String shortName = words[indexShortName];
            String longName = words[indexLongName];
            String type = words[indexType];

            Route route = new Route(routeID, agencyCode, shortName, longName, type);
            db.addRoute(route);

        }
        System.out.println(Route.getCount());

    }


    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }


}



