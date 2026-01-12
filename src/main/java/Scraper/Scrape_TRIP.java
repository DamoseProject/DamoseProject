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

/**
 * Modulo di scraping e parsing per i Viaggi (Trips) del sistema GTFS.
 * Questa classe analizza il file {@code trips.txt}, estraendo le informazioni che
 * definiscono le singole occorrenze di una linea su un percorso specifico.
 * * <p>La classe funge da ponte relazionale nel database, collegando le linee (Routes)
 * agli orari di passaggio. Include inoltre informazioni sull'accessibilità per
 * sedie a rotelle e la direzione della corsa.</p>
 */
public class Scrape_TRIP {

    /** Intestazioni GTFS standard per l'identificazione delle colonne nel file dei viaggi */
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("route_id","service_id","trip_id","trip_headsign","trip_short_name","direction_id","block_id","shape_id","wheelchair_accessible","exceptional"));

    /** Indice del riferimento alla linea (Route) di appartenenza */
    final private static int indexRouteID = titles.indexOf("route_id");

    /** Indice del riferimento al calendario di servizio (es. feriale, festivo) */
    final private static int indexServiceID = titles.indexOf("service_id");

    /** Indice dell'identificativo univoco del viaggio */
    final private static int indexTripID = titles.indexOf("trip_id");

    /** Indice della destinazione visualizzata sul display del bus (headsign) */
    final private static int indexTripHeadsign = titles.indexOf("trip_headsign");

    /** Indice del nome breve o identificativo della corsa */
    final private static int indexTripShortName = titles.indexOf("trip_short_name");

    /** Indice della direzione (0 per andata, 1 per ritorno) */
    final private static int indexDirectionID = titles.indexOf("direction_id");

    /** Indice del flag di accessibilità per disabili */
    final private static int indexWheelchairAccessible = titles.indexOf("wheelchair_accessible");


    /** Percorso locale del file sorgente dei viaggi */
    private static String filePath = "C:\\Users\\micko\\Downloads\\rome_static_gtfs (1)\\trips.txt";

    /**
     * Esegue la lettura del file e l'importazione dei viaggi nel database.
     * * <p>Il processo prevede:</p>
     * 1. Il caricamento del file in memoria e la suddivisione in righe.
     * 2. L'iterazione a partire dalla seconda riga (indice 1) per escludere l'header.
     * 3. La creazione di oggetti {@link Trip} con gestione degli errori per i campi numerici.
     * 4. Il salvataggio persistente tramite {@link Database#addTrip(Trip)}.
     * * @throws IOException Se il file al percorso specificato non è accessibile.
     * @throws SQLException Se si verifica un errore durante l'interazione con il database SQLite.
     */
    public static void scrape() throws IOException, SQLException {
        Database db = new Database();
        db.connect();

        String contentFile = Files.readString(Path.of(filePath));
        String[] lines = contentFile.split("\\r?\\n");

        System.out.println("Numero righe nel file (incluso header): " + lines.length);

        // Parti da 1 per saltare l'header
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            String[] words = line.split(",");

            String routeID = words[indexRouteID];
            String serviceID = words[indexServiceID];
            String tripID = words[indexTripID];
            String tripHeadsign = words[indexTripHeadsign];
            String tripShortName = words[indexTripShortName];
            String directionID = words[indexDirectionID];
            String wheelchairAccessible = words[indexWheelchairAccessible];

            Trip trip;
            try {
                trip = new Trip(tripID, routeID, serviceID, tripHeadsign, tripShortName, Integer.parseInt(directionID), Integer.parseInt(wheelchairAccessible));
            } catch (Exception e) {
                trip = new Trip(tripID, routeID, serviceID, tripHeadsign, tripShortName, -1, -1);
            }

            db.addTrip(trip);
            System.out.println("Trip inseriti: " + Trip.getCount());
        }
    }

    /**
     * Entry point per l'avvio del processo di scraping dei viaggi.
     * @param args Argomenti riga di comando.
     */
    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }





}
