package Scraper;

import model.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Modulo specializzato nel parsing e nell'importazione incrementale degli orari delle fermate.
 * La classe analizza il file {@code stop_times.txt} del dataset GTFS, mappando la relazione
 * temporale tra i viaggi (trips) e le singole fermate (stops).
 * * <p>Per gestire l'elevata mole di dati (spesso superiore alle centinaia di migliaia di righe),
 * il sistema utilizza:</p>
 * <ul>
 * <li><b>Batch Processing:</b> Inserimenti raggruppati ogni 1000 record per ottimizzare le prestazioni I/O.</li>
 * <li><b>Deduplicazione in memoria:</b> Utilizzo di un {@link HashSet} per confrontare le chiavi
 * composite (fermata|viaggio|orario) ed evitare record duplicati.</li>
 * <li><b>Gestione Transazionale:</b> Disabilitazione dell'AutoCommit per garantire la coerenza atomica dei batch.</li>
 * </ul>
 */
public class Scrape_missing_STOP_TIMES {

    /** Intestazioni GTFS standard per la mappatura delle colonne degli orari */
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("trip_id", "arrival_time", "departure_time", "stop_id", "stop_sequence", "stop_headsign", "pickup_type", "drop_off_type", "shape_dist_traveled", "timepoint"));
    final private static int indexArrivalTime = titles.indexOf("arrival_time");
    final private static int indexDepartureTime = titles.indexOf("departure_time");
    final private static int indexTripID = titles.indexOf("trip_id");
    final private static int indexStopID = titles.indexOf("stop_id");
    final private static int indexStopSequence = titles.indexOf("stop_sequence");
    final private static int indexStopHeadsign = titles.indexOf("stop_headsign");
    final private static int indexShapeDistTraveled = titles.indexOf("shape_dist_traveled");

    /** Percorso locale del file sorgente degli orari (Dataset statico) */
    private static String filePath = "C:\\Users\\micko\\Downloads\\rome_static_gtfs (1)\\stop_times.txt";

    /**
     * Esegue lo scraping del file e popola la tabella FERMATA_ORARIO nel database.
     * * Il metodo segue le seguenti fasi:
     * 1. Carica le chiavi esistenti dal DB per evitare duplicati.
     * 2. Legge il file riga per riga.
     * 3. Valida e trasforma i dati (es. gestione errori per sequenze o distanze mancanti).
     * 4. Esegue il commit dei dati in blocchi (batch) predefiniti.
     * * @throws IOException Se il file sorgente non è reperibile.
     * @throws SQLException Se si verificano errori durante le operazioni CRUD sul DB.
     */
    public static void scrapeAndAddToDatabase() throws IOException, SQLException {
        Database db = new Database();
        db.connect();
        db.getConnection().setAutoCommit(false);

        int BatchSize = 1000;
        int count = 0;

        // Carica tutte le chiavi già presenti nel DB in un HashSet per controlli veloci
        HashSet<String> existingKeys = new HashSet<>();
        var stmt = db.getConnection().createStatement();
        var rs = stmt.executeQuery("SELECT FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA FROM FERMATA_ORARIO");
        while (rs.next()) {
            String key = rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3);
            existingKeys.add(key);
        }
        rs.close();
        stmt.close();

        String contentFile = Files.readString(Path.of(filePath));

        String[] lines = contentFile.split("\\r?\\n");
        System.out.println("Numero righe nel file: " + lines.length);


        String sqlInsert = "INSERT INTO FERMATA_ORARIO (FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA, ORARIO_ARRIVO, FERMATA_SEQUENZA, TESTO_FERMATA, SHAPE_DIST_TRAVELED) VALUES (?, ?, ?, ?, ?, ? ,?)";
        PreparedStatement pstmtInsert = db.getConnection().prepareStatement(sqlInsert);

        int counterRow = 0;
        for (String line : lines) {
            if (false) {
                counterRow++;
                System.out.println(counterRow);
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

            String key = stopID + "|" + tripID + "|" + departureTime;
            if (!existingKeys.contains(key)) {
                existingKeys.add(key);  // aggiungi chiave per evitare duplicati nel batch

                try {
                    pstmtInsert.setString(1, stopID);
                    pstmtInsert.setString(2, tripID);
                    pstmtInsert.setString(3, departureTime);
                    pstmtInsert.setString(4, arrivalTime);
                    pstmtInsert.setInt(5, Integer.parseInt(stopSequence));
                    pstmtInsert.setString(6, stopHeadsign);
                    pstmtInsert.setInt(7, Integer.parseInt(shapeDistTraveled));
                } catch (Exception e) {
                    pstmtInsert.setString(1, stopID);
                    pstmtInsert.setString(2, tripID);
                    pstmtInsert.setString(3, departureTime);
                    pstmtInsert.setString(4, arrivalTime);
                    pstmtInsert.setInt(5, -1);
                    pstmtInsert.setString(6, stopHeadsign);
                    pstmtInsert.setInt(7, -1);
                }

                pstmtInsert.addBatch();
                count++;

                if (count % BatchSize == 0) {
                    pstmtInsert.executeBatch();
                    db.getConnection().commit();
                }
            }
            counterRow++;
            System.out.println(counterRow);
        }
        pstmtInsert.executeBatch();
        db.getConnection().commit();

        pstmtInsert.close();
    }

    /**
     * Entry point per l'esecuzione manuale dell'aggiornamento orari.
     * @param args Argomenti riga di comando.
     */
    public static void main(String[] args) throws IOException, SQLException {
        scrapeAndAddToDatabase();
    }
}
