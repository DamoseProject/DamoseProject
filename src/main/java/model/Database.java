package model;

import util.TimeComparator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database {

    final private static String DATABASE_LINK = "jdbc:sqlite:RomeBusDatabase.db";
    private static Connection connection;

    public Database() {
    }

    // ==================================================================================
    // 1. GESTIONE CONNESSIONE E CREAZIONE DB
    // ==================================================================================

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            // Crea il file se non esiste
            connection = DriverManager.getConnection(DATABASE_LINK);

            // Crea le tabelle se non esistono (Primo avvio)
            createNewDatabaseStructure();

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Crea la struttura delle tabelle se il database è nuovo o vuoto.
     */
    private void createNewDatabaseStructure() throws SQLException {
        Statement stmt = connection.createStatement();

        // UTENTE
        stmt.execute("CREATE TABLE IF NOT EXISTS UTENTE (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOME TEXT, COGNOME TEXT, USERNAME TEXT UNIQUE, " +
                "EMAIL TEXT UNIQUE, PASSWORD TEXT)");

        // FERMATA
        stmt.execute("CREATE TABLE IF NOT EXISTS Fermata (" +
                "ID TEXT PRIMARY KEY, " +
                "CODICE TEXT, NOME TEXT, " +
                "LATITUDINE REAL, LONGITUDINE REAL)");

        // PERCORSO (Routes)
        stmt.execute("CREATE TABLE IF NOT EXISTS Percorso (" +
                "ID TEXT PRIMARY KEY, " +
                "AGENZIA_ID TEXT, NOME_BREVE TEXT, " +
                "NOME_COMPLETO TEXT, TIPO TEXT)");

        // VIAGGIO (Trips)
        stmt.execute("CREATE TABLE IF NOT EXISTS Viaggio (" +
                "ID TEXT PRIMARY KEY, " +
                "PERCORSO_ID TEXT, SERVIZIO_ID TEXT, " +
                "TESTO_DESTINAZIONE TEXT, NOME_BREVE TEXT, " +
                "DIREZIONE INTEGER, ACCESSIBILE_DIVERSAMENTE_ABILI INTEGER, " +
                "FOREIGN KEY(PERCORSO_ID) REFERENCES Percorso(ID))");

        // FERMATA_ORARIO (Stop Times)
        stmt.execute("CREATE TABLE IF NOT EXISTS FERMATA_ORARIO (" +
                "FERMATA_ID TEXT, VIAGGIO_ID TEXT, " +
                "ORARIO_PARTENZA TEXT, ORARIO_ARRIVO TEXT, " +
                "FERMATA_SEQUENZA INTEGER, TESTO_FERMATA TEXT, " +
                "SHAPE_DIST_TRAVELED INTEGER, " +
                "FOREIGN KEY(FERMATA_ID) REFERENCES Fermata(ID), " +
                "FOREIGN KEY(VIAGGIO_ID) REFERENCES Viaggio(ID))");

        // INDICI (Performance)
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_fermata_orario_fermata ON FERMATA_ORARIO(FERMATA_ID)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_fermata_orario_viaggio ON FERMATA_ORARIO(VIAGGIO_ID)");

        // PREFERITI
        stmt.execute("CREATE TABLE IF NOT EXISTS PREFERITI_FERMATE (" +
                "UTENTE_ID INTEGER, FERMATA_ID TEXT, " +
                "PRIMARY KEY (UTENTE_ID, FERMATA_ID), " +
                "FOREIGN KEY(UTENTE_ID) REFERENCES UTENTE(ID), " +
                "FOREIGN KEY(FERMATA_ID) REFERENCES Fermata(ID))");

        // BUS (Flotta)
        stmt.execute("CREATE TABLE IF NOT EXISTS Bus (" +
                "ID TEXT PRIMARY KEY, LABEL TEXT, LICENSE_PLATE TEXT)");

        stmt.close();
    }

    // ==================================================================================
    // 2. GESTIONE UTENTI (AUTENTICAZIONE E RECUPERO DATI)
    // ==================================================================================

    public int addUser(User user) throws SQLException {
        try {
            String sql = "INSERT INTO UTENTE(NOME, COGNOME, USERNAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.executeUpdate();
            pstmt.close();
            return 0; /// Aggiunto senza errori
        }
        catch(Exception e) {
            return 1; /// Si è verificato un errore.
        }
    }

    public boolean isUserRegistered(String user) throws SQLException {
        String query = "SELECT * FROM UTENTE WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, user);
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next();
    }

    public boolean isEmailRegistered(String email) throws SQLException {
        String query = "SELECT * FROM UTENTE WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, email);
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next();
    }

    public User getUser(int id) throws SQLException {
        String sql = "SELECT * FROM UTENTE WHERE ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new User(rs.getInt("ID"), rs.getString("NOME"), rs.getString("COGNOME"), rs.getString("USERNAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM UTENTE WHERE USERNAME = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new User(rs.getInt("ID"), rs.getString("NOME"), rs.getString("COGNOME"), rs.getString("USERNAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM UTENTE WHERE EMAIL = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, email);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new User(rs.getInt("ID"), rs.getString("NOME"), rs.getString("COGNOME"), rs.getString("USERNAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));
        }
        pstmt.close();
        return null;
    }

    // ==================================================================================
    // 3. LETTURA DATI GTFS (FERMATE, LINEE, BUS - BASE)
    // ==================================================================================

    public Stop getStop(String id) throws SQLException {
        String sql = "SELECT * FROM Fermata WHERE ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Stop(rs.getString("ID"), rs.getString("CODICE"), rs.getString("NOME"), rs.getFloat("LATITUDINE"), rs.getFloat("LONGITUDINE"));
        }
        return null;
    }

    public ArrayList<Stop> getStops() throws SQLException {
        ArrayList<Stop> stops = new ArrayList<>();
        String sql = "SELECT * FROM Fermata";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            stops.add(new Stop(rs.getString("ID"), rs.getString("CODICE"), rs.getString("NOME"), rs.getFloat("LATITUDINE"), rs.getFloat("LONGITUDINE")));
        }
        rs.close();
        pstmt.close();
        return stops;
    }

    public List<Stop> getStopsByName(String name) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        String sql = "SELECT * FROM Fermata WHERE NOME LIKE ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, "%" + name + "%");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            stops.add(new Stop(rs.getString("ID"), rs.getString("CODICE"), rs.getString("NOME"), rs.getFloat("LATITUDINE"), rs.getFloat("LONGITUDINE")));
        }
        pstmt.close();
        return stops;
    }

    public Route getRoute(String id) throws SQLException {
        String sql = "SELECT * FROM Percorso WHERE ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Route(rs.getString("ID"), rs.getString("AGENZIA_ID"), rs.getString("NOME_BREVE"), rs.getString("NOME_COMPLETO"), rs.getString("TIPO"));
        }
        return null;
    }

    public List<Route> getRoutesByName(String name) throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM PERCORSO WHERE NOME_BREVE LIKE ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        pstmt.close();
        while (rs.next()) {
            routes.add(new Route(rs.getString("ID"), rs.getString("AGENZIA_ID"), rs.getString("NOME_BREVE"), rs.getString("NOME_COMPLETO"), rs.getString("TIPO")));
        }
        return routes;
    }

    public Trip getTrip(String id) throws SQLException {
        String sql = "SELECT * FROM Viaggio WHERE ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Trip(rs.getString("ID"), rs.getString("PERCORSO_ID"), rs.getString("SERVIZIO_ID"), rs.getString("TESTO_DESTINAZIONE"), rs.getString("NOME_BREVE"), rs.getInt("DIREZIONE"), rs.getInt("ACCESSIBILE_DIVERSAMENTE_ABILI"));
        }
        return null;
    }

    public ArrayList<Bus> getBusList() throws SQLException {
        ArrayList<Bus> busList = new ArrayList<>();
        String sql = "SELECT * FROM Bus ORDER BY ID";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            busList.add(new Bus(rs.getString("ID"), rs.getString("LABEL"), rs.getString("LICENSE_PLATE")));
        }
        rs.close();
        pstmt.close();
        return busList;
    }

    public ArrayList<String> getIdBusList() throws SQLException {
        ArrayList<String> idList = new ArrayList<>();
        String sql = "SELECT ID FROM Bus ORDER BY ID";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            idList.add(rs.getString("ID"));
        }
        rs.close();
        pstmt.close();
        return idList;
    }

    // ==================================================================================
    // 4. QUERY COMPLESSE: ORARI E ARRIVI
    // ==================================================================================

    public List<BusInUnaFermataRecord> getBusInUnaFermataRecord(String stopId) throws SQLException {
        List<BusInUnaFermataRecord> busInUnaFermataRecords = new ArrayList<>();
        String sql = "SELECT VIAGGIO_ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE,  Viaggio.NOME_BREVE, DIREZIONE, ORARIO_ARRIVO, ORARIO_PARTENZA\n" +
                "FROM (FERMATA_ORARIO INNER JOIN Fermata ON FERMATA_ID = Fermata.ID) INNER JOIN Viaggio ON FERMATA_ORARIO.VIAGGIO_ID = Viaggio.ID\n" +
                "WHERE FERMATA_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, stopId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            busInUnaFermataRecords.add(new BusInUnaFermataRecord(rs.getString("VIAGGIO_ID"), rs.getString("PERCORSO_ID"), rs.getString("SERVIZIO_ID"), rs.getString("TESTO_DESTINAZIONE"), rs.getString("NOME_BREVE"), rs.getInt("DIREZIONE"), rs.getString("ORARIO_ARRIVO"), rs.getString("ORARIO_PARTENZA")));
        }
        rs.close();
        pstmt.close();

        busInUnaFermataRecords.sort( (a,b) -> a.getArrivalTime().compareTo(b.getArrivalTime()) );

        return busInUnaFermataRecords;
    }

    public List<BusInUnaFermataRecord> getArriviDiUnaLineaInUnaFermata(String stopId, String routeId) throws SQLException {
        List<BusInUnaFermataRecord> busInUnaFermataRecords = new ArrayList<>();
        String sql = "SELECT VIAGGIO_ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE,  Viaggio.NOME_BREVE, DIREZIONE, ORARIO_ARRIVO, ORARIO_PARTENZA\n" +
                "FROM (FERMATA_ORARIO INNER JOIN Fermata ON FERMATA_ID = Fermata.ID) INNER JOIN Viaggio ON FERMATA_ORARIO.VIAGGIO_ID = Viaggio.ID\n" +
                "WHERE FERMATA_ID = ? AND PERCORSO_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, stopId);
        pstmt.setString(2, routeId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            busInUnaFermataRecords.add(new BusInUnaFermataRecord(rs.getString("VIAGGIO_ID"), rs.getString("PERCORSO_ID"), rs.getString("SERVIZIO_ID"), rs.getString("TESTO_DESTINAZIONE"), rs.getString("NOME_BREVE"), rs.getInt("DIREZIONE"), rs.getString("ORARIO_ARRIVO"), rs.getString("ORARIO_PARTENZA")));
        }
        rs.close();

        busInUnaFermataRecords.sort((a, b) -> a.getArrivalTime().compareTo(b.getArrivalTime()));
        return busInUnaFermataRecords;
    }

    public BusInUnaFermataRecord getProssimoArrivoInUnaFermata(String stopId) throws SQLException {
        List<BusInUnaFermataRecord> records = getBusInUnaFermataRecord(stopId);
        return getProssimoPerOrario(records);
    }

    public BusInUnaFermataRecord getProssimoArrivoInUnaLineaInUnaFermata(String stopId, String routeId) throws SQLException {
        List<BusInUnaFermataRecord> records = getArriviDiUnaLineaInUnaFermata(stopId, routeId);
        return getProssimoPerOrario(records);
    }


    public BusInUnaFermataRecord getProssimoArrivoInUnaFermataDiUnaLineaRealTime(String stopId, String routeID) throws SQLException {
        List<BusInUnaFermataRecord> records = getArriviDiUnaLineaInUnaFermata(stopId, routeID);
        BusInUnaFermataRecord prossimo = getProssimoPerOrario(records);

        if (prossimo == null) {
            return null;
        }


        Optional<Integer> delay = RealTimeHandler.getRitardo(prossimo.getTripId());

        if (delay.isPresent()) {
            prossimo.setRitardoInSecondi(delay.get());
        }
        return prossimo;


    }



    // ==================================================================================
    // 5. PREFERITI UTENTE
    // ==================================================================================

    public void addUserFavouriteStop(User user, Stop stop) throws SQLException {
        String sql = "INSERT INTO PREFERITI_FERMATE (UTENTE_ID, FERMATA_ID) VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, user.getId());
        pstmt.setString(2, stop.getId());
        pstmt.executeUpdate();
        pstmt.close();
    }

    public List<Stop> getFavouriteStopsByUser(User user) throws SQLException {
        String sql = "SELECT * FROM PREFERITI_FERMATE WHERE UTENTE_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, user.getId());
        ResultSet rs = pstmt.executeQuery();
        List<Stop> stops = new ArrayList<>();
        while (rs.next()) {
            stops.add(getStop(rs.getString("FERMATA_ID")));
        }
        rs.close();
        pstmt.close();
        return stops;
    }

    // ==================================================================================
    // 6. INSERIMENTO DATI (SCRAPER)
    // ==================================================================================

    public void addBus(Bus bus) throws SQLException {
        String sql = "INSERT INTO Bus (ID, LABEL, LICENSE_PLATE) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, bus.getIdBus());
        pstmt.setString(2,bus.getLabelBus());
        pstmt.setString(3,bus.getLicensePlate());
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void addStop(Stop stop) throws SQLException {
        String sql = "INSERT INTO Fermata (ID, CODICE, NOME, LATITUDINE, LONGITUDINE) VALUES (?, ?, ?, ?, ? )";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, stop.getId());
        pstmt.setString(2, stop.getCode());
        pstmt.setString(3, stop.getName());
        pstmt.setFloat(4, stop.getLatitude());
        pstmt.setFloat(5, stop.getLongitude());
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void addRoute(Route route) throws SQLException {
        String sql = "INSERT INTO Percorso (ID, AGENZIA_ID, NOME_BREVE, NOME_COMPLETO, TIPO) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, route.getId());
        pstmt.setString(2, route.getAgencyCode());
        pstmt.setString(3, route.getShortName());
        pstmt.setString(4, route.getLongName());
        pstmt.setString(5, route.getType());
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void addTrip(Trip trip) throws SQLException {
        String sql = "INSERT INTO Viaggio (ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE, NOME_BREVE, DIREZIONE, ACCESSIBILE_DIVERSAMENTE_ABILI) VALUES (?, ?, ?, ?, ?,?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, trip.getId());
        pstmt.setString(2, trip.getRouteId());
        pstmt.setString(3, trip.getServiceId());
        pstmt.setString(4, trip.getTripHeadsign());
        pstmt.setString(5, trip.getTripShortName());
        pstmt.setInt(6, trip.getDirection());
        pstmt.setInt(7, trip.getWheelchair_accessible());
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void addStopTime(StopTime stopTime) throws SQLException {
        String sql = "INSERT INTO FERMATA_ORARIO (FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA, ORARIO_ARRIVO, FERMATA_SEQUENZA, TESTO_FERMATA, SHAPE_DIST_TRAVELED) VALUES (?, ?, ?, ?, ?, ? ,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1,stopTime.getStopID());
        pstmt.setString(2,stopTime.getTripID());
        pstmt.setString(3,stopTime.getDepartureTime());
        pstmt.setString(4,stopTime.getArrivalTime());
        pstmt.setInt(5,stopTime.getStopSequence());
        pstmt.setString(6, stopTime.getStopHeadsign());
        pstmt.setInt(7, stopTime.getShapeDistTraveled());
        pstmt.executeUpdate();
        pstmt.close();
    }

    // ==================================================================================
    // 7. METODI DI UTILITÀ (PRIVATI)
    // ==================================================================================

    private BusInUnaFermataRecord getProssimoPerOrario(List<BusInUnaFermataRecord> records){
        TimeComparator timeComparator = new TimeComparator();
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        for(int i = 0; i < records.size(); i++) {
            if(timeComparator.compare(records.get(i).getArrivalTime(), nowStr) > 0 ) {
                return records.get(i);
            }
        }
        // Se la lista è vuota o sono passati tutti, gestisci il caso (ritorna null o il primo della lista)
        if (records.isEmpty()) return null;
        return records.get(0);
    }

    private List<BusInUnaFermataRecord> getProssimiPerOrario(List<BusInUnaFermataRecord> records){
        TimeComparator timeComparator = new TimeComparator();
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        for(int i = 0; i < records.size(); i++) {
            if(timeComparator.compare(records.get(i).getArrivalTime(), nowStr) > 0 ) {
                return records.subList(i, records.size());
            }
        }
        return records;
    }
}