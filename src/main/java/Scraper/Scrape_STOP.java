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

/**
 * Modulo di scraping e parsing per le fermate fisiche (Stops) del dataset GTFS.
 * La classe analizza il file {@code stops.txt}, estraendo le informazioni anagrafiche
 * e geografiche di ogni fermata della rete di trasporto di Roma.
 * * <p>Il processo converte le stringhe di latitudine e longitudine in valori {@code float},
 * permettendo l'integrazione con le librerie di mapping e il calcolo delle distanze
 * all'interno del database SQLite.</p>
 */
public class Scrape_STOP {

    /** Intestazioni GTFS standard per l'identificazione delle colonne nel file delle fermate */
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "stop_url", "wheelchair_boarding", "stop_timezone", "location_type", "parent_station"));

    /** Indice dell'identificativo interno GTFS della fermata */
    final private static int indexStopID = titles.indexOf("stop_id") ;

    /** Indice del codice pubblico della fermata (quello visibile sulle paline) */
    final private static int indexStopCode = titles.indexOf("stop_code") ;

    /** Indice del nome descrittivo della fermata (es. "Piazza Venezia") */
    final private static int indexStopName = titles.indexOf("stop_name") ;

    /** Indice della coordinata geografica Latitudine */
    final private static int indexStopLat = titles.indexOf("stop_lat") ;

    /** Indice della coordinata geografica Longitudine */
    final private static int indexStopLon = titles.indexOf("stop_lon") ;

    /** Percorso locale del file sorgente delle fermate (Dataset statico) */
    private static String filePath = "C:\\Users\\micko\\Downloads\\rome_static_gtfs (1)\\stops.txt";

    /**
     * Esegue la lettura del file e il caricamento delle fermate nel database.
     * * <p>La logica prevede:</p>
     * 1. La lettura massiva delle linee tramite {@link Files#readAllLines(Path)}.
     * 2. Il parsing riga per riga con estrazione dei campi tramite delimitatore virgola.
     * 3. La conversione delle coordinate in formato numerico.
     * 4. Il salvataggio nel DB tramite {@link Database#addStop(Stop)}.
     * * <p>Include un blocco di cattura per {@link NumberFormatException} per ignorare
     * eventuali righe di intestazione o record con coordinate malformate.</p>
     * * @throws IOException Se il file al percorso specificato non è accessibile.
     * @throws SQLException Se si verifica un errore durante l'inserimento SQL.
     */
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

    /**
     * Entry point per l'avvio manuale dell'importazione delle fermate.
     * @param args Argomenti riga di comando.
     */
    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }


}
