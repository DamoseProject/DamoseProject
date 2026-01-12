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

/**
 * Modulo di scraping e parsing per le linee di trasporto (Routes) del sistema GTFS.
 * La classe si occupa di leggere il file {@code routes.txt}, estrarre le informazioni
 * identificative delle linee (ID, codice agenzia, nome breve e lungo) e popolarne
 * la relativa tabella nel database.
 * * <p>Utilizza indici statici per mappare le colonne del file CSV, garantendo
 * che i dati vengano estratti correttamente indipendentemente dall'ordine nel file originale.</p>
 */
public class Scape_ROUTES {

    /** Intestazioni previste nel file routes.txt per l'identificazione delle colonne */
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("route_id", "agency_id","route_short_name","route_long_name","route_type","route_url","route_color","route_text_color"));

    /** Indice della colonna contenente l'identificativo univoco della linea */
    final private static int indexRouteID = titles.indexOf("route_id") ;

    /** Indice della colonna contenente l'ID dell'agenzia di trasporto (es. ATAC) */
    final private static int indexAgencyCode = titles.indexOf("agency_id") ;

    /** Indice del nome breve della linea (es. "64", "H", "Metro A") */
    final private static int indexShortName = titles.indexOf("route_short_name") ;

    /** Indice del nome esteso o descrizione della linea */
    final private static int indexLongName = titles.indexOf("route_long_name") ;

    /** Indice della tipologia di mezzo (es. 3 per bus, 1 per metro) */
    final private static int indexType = titles.indexOf("route_type") ;

    /** Percorso locale del file sorgente GTFS */
    private static String filePath = "/home/carmine/Scaricati/rome_static_gtfs/routes.txt";

    /**
     * Esegue la lettura del file e l'importazione dei dati nel database.
     * Legge l'intero contenuto del file come stringa, suddivide le righe e,
     * per ciascuna riga, crea un oggetto {@link Route} che viene aggiunto
     * al database tramite {@link Database#addRoute(Route)}.
     * * @throws IOException Se il file al percorso specificato non è accessibile.
     * @throws SQLException Se si verifica un errore durante l'inserimento SQL.
     */
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


    /**
     * Punto di ingresso per l'esecuzione isolata dello scraper delle linee.
     * * @param args Argomenti da riga di comando (non utilizzati).
     * @throws IOException Errore di lettura file.
     * @throws SQLException Errore database.
     */
    public static void main(String[] args) throws IOException, SQLException {
        scrape();
    }


}



