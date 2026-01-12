package Scraper;

import model.Database;
import model.StopTime;
import model.Trip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Modulo di scraping e importazione massiva degli orari delle fermate (Stop Times).
 * La classe processa il file {@code stop_times.txt}, che costituisce il dataset più
 * voluminoso dello standard GTFS, mappando ogni viaggio (trip) alle fermate
 * intermedie con i relativi orari di arrivo e partenza.
 * * <p>Caratteristiche tecniche principali:</p>
 * <ul>
 * <li><b>Resume Capability:</b> Include un meccanismo di salto righe per riprendere
 * l'importazione in caso di interruzione.</li>
 * <li><b>Batch Processing:</b> Raggruppa gli inserimenti in lotti da 1000 record per
 * ridurre il numero di transazioni I/O sul disco.</li>
 * <li><b>Fault Tolerance:</b> Gestisce dati numerici mancanti o malformati assegnando
 * valori di default (-1) senza interrompere il processo.</li>
 * </ul>
 */
public class Scrape_STOP_TIMES {

    /** Intestazioni GTFS utilizzate per mappare dinamicamente le colonne del file di testo */
    final private static ArrayList<String> titles = new ArrayList<>(Arrays.asList("trip_id", "arrival_time", "departure_time", "stop_id", "stop_sequence", "stop_headsign", "pickup_type", "drop_off_type", "shape_dist_traveled", "timepoint"));
    final private static int indexArrivalTime = titles.indexOf("arrival_time");
    final private static int indexDepartureTime = titles.indexOf("departure_time");
    final private static int indexTripID = titles.indexOf("trip_id");
    final private static int indexStopID = titles.indexOf("stop_id");
    final private static int indexStopSequence = titles.indexOf("stop_sequence");
    final private static int indexStopHeadsign = titles.indexOf("stop_headsign");
    final private static int indexShapeDistTraveled = titles.indexOf("shape_dist_traveled");

    /** Percorso del file stop_times.txt all'interno del dataset locale */
    private static String filePath = "/home/carmine/Scaricati/rome_static_gtfs/stop_times.txt";





    /**
     * Esegue il parsing del file e popola la tabella FERMATA_ORARIO.
     * * Il metodo disabilita l'AutoCommit per migliorare le prestazioni e utilizza
     * {@link PreparedStatement} per l'esecuzione di batch SQL. Include una logica
     * di skipping per saltare le righe già elaborate in sessioni precedenti.
     * * @throws IOException In caso di problemi nella lettura del file GTFS.
     * @throws SQLException In caso di errori nell'esecuzione delle query SQL.
     */
    public static void scrapeAndAddToDatabase() throws IOException, SQLException {
        Database db = new Database();
        db.connect();
        db.getConnection().setAutoCommit(false);

        int BatchSize = 1000;
        int count = 0;


        String contentFile = Files.readString(Path.of(filePath));
        String[] lines = contentFile.split(System.lineSeparator());

        String sql = "INSERT INTO FERMATA_ORARIO (FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA, ORARIO_ARRIVO, FERMATA_SEQUENZA, TESTO_FERMATA, SHAPE_DIST_TRAVELED) VALUES (?, ?, ?, ?, ?, ? ,?)";
        PreparedStatement pstmt = db.getConnection().prepareStatement(sql);

        int counterRow = 0;
        for (String line : lines) {
            if (counterRow <= 1248455) {
                counterRow++;
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

            try {
                pstmt.setString(1, stopID);
                pstmt.setString(2, tripID);
                pstmt.setString(3, departureTime);
                pstmt.setString(4, arrivalTime);
                pstmt.setInt(5, Integer.parseInt(stopSequence));
                pstmt.setString(6, stopHeadsign);
                pstmt.setInt(7, Integer.parseInt(shapeDistTraveled));
            } catch (Exception e) {
                pstmt.setString(1, stopID);
                pstmt.setString(2, tripID);
                pstmt.setString(3, departureTime);
                pstmt.setString(4, arrivalTime);
                pstmt.setInt(5, -1);
                pstmt.setString(6, stopHeadsign);
                pstmt.setInt(7, -1);
            }
            pstmt.addBatch();
            count++;
            counterRow++;
            System.out.println(counterRow);

            if (count % BatchSize == 0) {
                pstmt.executeBatch();
                db.getConnection().commit();
            }

        }
        pstmt.executeBatch();
        db.getConnection().commit();
        pstmt.close();


    }

    /**
     * Entry point per l'esecuzione del processo di importazione orari.
     * @param args Argomenti da riga di comando.
     */
    public static void main(String[] args) throws IOException, SQLException {
        scrapeAndAddToDatabase();
    }


}
