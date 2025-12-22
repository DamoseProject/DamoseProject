package Scraper;

import model.Database;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateData {

    // === 1. PERCORSI FILE ===
    private static final String BASE_PATH = GtfsDownloader.DOWNLOAD_DIR;

    private static final String PATH_ROUTES = BASE_PATH + "routes.txt";
    private static final String PATH_STOPS = BASE_PATH + "stops.txt";
    private static final String PATH_TRIPS = BASE_PATH + "trips.txt";
    private static final String PATH_STOP_TIMES = BASE_PATH + "stop_times.txt";

    // === 2. QUERY SQL ===

    // Tabella: Percorso
    private static final String SQL_INSERT_ROUTE =
            "INSERT INTO Percorso (ID, AGENZIA_ID, NOME_BREVE, NOME_COMPLETO, TIPO) VALUES (?, ?, ?, ?, ?)";

    // Tabella: Fermata
    private static final String SQL_INSERT_STOP =
            "INSERT INTO Fermata (ID, CODICE, NOME, LATITUDINE, LONGITUDINE) VALUES (?, ?, ?, ?, ?)";

    // Tabella: Viaggio (Nota: ID è il primo campo nel DB, ma trip_id è spesso il 3° nel file txt)
    private static final String SQL_INSERT_TRIP =
            "INSERT INTO Viaggio (ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE, NOME_BREVE, DIREZIONE, ACCESSIBILE_DIVERSAMENTE_ABILI) VALUES (?, ?, ?, ?, ?, ?, ?)";

    // Tabella: FERMATA_ORARIO
    private static final String SQL_INSERT_STOP_TIME =
            "INSERT INTO FERMATA_ORARIO (FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA, ORARIO_ARRIVO, FERMATA_SEQUENZA, TESTO_FERMATA, SHAPE_DIST_TRAVELED) VALUES (?, ?, ?, ?, ?, ? ,?)";

    public static void updateAll() {
        Database db = new Database();
        Connection conn = null;

        try {
            db.connect();
            conn = db.getConnection();

            conn.setAutoCommit(false);

            System.out.println("=== INIZIO AGGIORNAMENTO DATABASE GTFS ===");
            long startTime = System.currentTimeMillis();

            cleanDatabase(conn);

            loadStops(conn);      // Popola Fermata
            loadRoutes(conn);     // Popola Percorso
            loadTrips(conn);      // Popola Viaggio
            loadStopTimes(conn);  // Popola FERMATA_ORARIO

            conn.commit(); // Salva tutto permanentemente

            long endTime = System.currentTimeMillis();
            System.out.println("=== AGGIORNAMENTO COMPLETATO IN " + (endTime - startTime)/1000 + " SECONDI ===");

        } catch (Exception e) {
            System.err.println("!!! ERRORE CRITICO. ROLLBACK IN CORSO... !!!");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Annulla tutto in caso di errore
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private static void cleanDatabase(Connection conn) throws SQLException {
        System.out.println(">> [1/5] Cancellazione vecchi dati...");
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM FERMATA_ORARIO");
        stmt.executeUpdate("DELETE FROM Viaggio");
        stmt.executeUpdate("DELETE FROM Percorso");
        stmt.executeUpdate("DELETE FROM Fermata");
        stmt.close();
        System.out.println("   -> Database pulito.");
    }

    private static void loadStops(Connection conn) throws IOException, SQLException {
        System.out.println(">> [2/5] Caricamento Fermate...");
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_STOPS));
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_STOP)) {

            String line = br.readLine(); // salto l' Header
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(",");
                try {
                    pstmt.setString(1, words[0]); // ID
                    pstmt.setString(2, words[1]); // CODICE
                    pstmt.setString(3, words[2]); // NOME
                    pstmt.setFloat(4, Float.parseFloat(words[4])); // LATITUDINE
                    pstmt.setFloat(5, Float.parseFloat(words[5])); // LONGITUDINE
                    pstmt.addBatch();
                    count++;
                } catch (Exception e) {}

                if (count % 1000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
            System.out.println("   -> Fermate inserite: " + count);
        }
    }

    private static void loadRoutes(Connection conn) throws IOException, SQLException {
        System.out.println(">> [3/5] Caricamento Percorsi...");
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_ROUTES));
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_ROUTE)) {

            String line = br.readLine(); // Header
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(",");
                try {
                    // Mapping: route_id(0), agency_id(1), short_name(2), long_name(3), type(4)
                    pstmt.setString(1, words[0]); // ID
                    pstmt.setString(2, words[1]); // AGENZIA_ID
                    pstmt.setString(3, words[2]); // NOME_BREVE
                    pstmt.setString(4, words[3]); // NOME_COMPLETO
                    pstmt.setString(5, words[4]); // TIPO
                    pstmt.addBatch();
                    count++;
                } catch (Exception e) {}

                if (count % 1000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
            System.out.println("   -> Percorsi inseriti: " + count);
        }
    }

    private static void loadTrips(Connection conn) throws IOException, SQLException {
        System.out.println(">> [4/5] Caricamento Viaggi...");
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TRIPS));
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_TRIP)) {

            String line = br.readLine(); // Header
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(",");
                try {
                    // File: route_id(0), service_id(1), trip_id(2), headsign(3), short_name(4), direction(5)... wheelchair(8)
                    // DB: ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE, NOME_BREVE, DIREZIONE, ACCESSIBILE

                    pstmt.setString(1, words[2]); // ID (trip_id nel file è indice 2)
                    pstmt.setString(2, words[0]); // PERCORSO_ID (route_id)
                    pstmt.setString(3, words[1]); // SERVIZIO_ID
                    pstmt.setString(4, words[3]); // TESTO_DESTINAZIONE
                    pstmt.setString(5, words[4]); // NOME_BREVE
                    pstmt.setInt(6, Integer.parseInt(words[5])); // DIREZIONE

                    try {
                        pstmt.setInt(7, Integer.parseInt(words[8])); // ACCESSIBILE
                    } catch (Exception e) { pstmt.setInt(7, 0); } // Default 0 se manca

                    pstmt.addBatch();
                    count++;
                } catch (Exception e) {}

                if (count % 2000 == 0) pstmt.executeBatch();
            }
            pstmt.executeBatch();
            System.out.println("   -> Viaggi inseriti: " + count);
        }
    }

    private static void loadStopTimes(Connection conn) throws IOException, SQLException {
        System.out.println(">> [5/5] Caricamento Orari (Attendi...)...");
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_STOP_TIMES));
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_STOP_TIME)) {

            String line = br.readLine(); // Header
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(",");
                try {
                    // File: trip_id(0), arrival(1), departure(2), stop_id(3), seq(4), headsign(5)... dist(8)
                    // DB: FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA, ORARIO_ARRIVO, FERMATA_SEQUENZA, TESTO_FERMATA, SHAPE

                    pstmt.setString(1, words[3]); // FERMATA_ID
                    pstmt.setString(2, words[0]); // VIAGGIO_ID
                    pstmt.setString(3, words[2]); // ORARIO_PARTENZA
                    pstmt.setString(4, words[1]); // ORARIO_ARRIVO
                    pstmt.setInt(5, Integer.parseInt(words[4])); // FERMATA_SEQUENZA
                    pstmt.setString(6, words[5]); // TESTO_FERMATA

                    try {
                        pstmt.setInt(7, Integer.parseInt(words[8])); // SHAPE_DIST_TRAVELED
                    } catch (Exception e) { pstmt.setInt(7, -1); }

                    pstmt.addBatch();
                    count++;
                } catch (Exception e) {}

                if (count % 5000 == 0) {
                    pstmt.executeBatch();
                    if (count % 100000 == 0) System.out.println("      ... processate " + count + " righe");
                }
            }
            pstmt.executeBatch();
            System.out.println("   -> Orari inseriti: " + count);
        }
    }



    public static void updateIfNew(){
        boolean nuoviDatiScaricati = GtfsDownloader.downloadIfNew();

        if (nuoviDatiScaricati) {
            System.out.println("Rilevati nuovi file GTFS. Avvio aggiornamento Database...");
            updateAll();
        } else {
            System.out.println("Database già allineato all'ultima versione GTFS. Nessuna operazione richiesta.");

        }
    }

    public static void main(String[] args) {
        updateIfNew();
    }


}