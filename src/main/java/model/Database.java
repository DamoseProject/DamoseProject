package model;

import Scraper.UpdateData;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.util.Optional;


public class Database {

    final private static String DATABASE_LINK = "jdbc:sqlite:RomeBusDatabase.db";
    private static Connection connection;

    public Database() {
    }

    // ==================================================================================
    // 1. GESTIONE CONNESSIONE E CREAZIONE DB
    // ==================================================================================

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");


            File file = new File("RomeBusDatabase.db");
            boolean isFirstRun = false;
            if (!file.exists())
                isFirstRun = true;




            connection = DriverManager.getConnection(DATABASE_LINK);
            createNewDatabaseStructure();
            System.out.println("Connection to SQLite has been established.");
            if (isFirstRun) {
                System.out.println("Database created.");
                UpdateData.updateIfNew();
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public Connection getConnection() {
        return connection;
    }

    private void createNewDatabaseStructure() throws SQLException {
        // Uso try-with-resources per garantire la chiusura dello Statement
        try (Statement stmt = connection.createStatement()) {

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

            // PREFERITI FERMATE
            stmt.execute("CREATE TABLE IF NOT EXISTS PREFERITI_FERMATE (" +
                    "UTENTE_ID INTEGER, FERMATA_ID TEXT, " +
                    "PRIMARY KEY (UTENTE_ID, FERMATA_ID), " +
                    "FOREIGN KEY(UTENTE_ID) REFERENCES UTENTE(ID), " +
                    "FOREIGN KEY(FERMATA_ID) REFERENCES Fermata(ID))");

            // LINEE PREFERITE
            stmt.execute("CREATE TABLE IF NOT EXISTS PREFERITI_LINEE(" +
                    "UTENTE_ID INTEGER, PERCORSO_ID TEXT, " +
                    "FOREIGN KEY(UTENTE_ID) REFERENCES UTENTE(ID), " +
                    "FOREIGN KEY(PERCORSO_ID) REFERENCES Percorso(ID))");

            // BUS (Flotta)
            stmt.execute("CREATE TABLE IF NOT EXISTS Bus (" +
                    "ID TEXT PRIMARY KEY, LABEL TEXT, LICENSE_PLATE TEXT)");


            stmt.execute("CREATE TABLE IF NOT EXISTS STORICO_PERFORMANCE (" +
                    "ROUTE_ID TEXT, " +
                    "STOP_ID TEXT, " +
                    "RITARDO_RILEVATO INTEGER, " + // in secondi
                    "CORSA_SALTATA INTEGER, " +    // 0 = passata, 1 = saltata
                    "DATA_OSSERVAZIONE DATE DEFAULT CURRENT_DATE)");


        }
    }

    // ==================================================================================
    // 2. GESTIONE UTENTI
    // ==================================================================================

    public int addUser(User user) {
        String sql = "INSERT INTO UTENTE(NOME, COGNOME, USERNAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.executeUpdate();
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public boolean isUserRegistered(String user) throws SQLException {
        String query = "SELECT * FROM UTENTE WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isEmailRegistered(String email) throws SQLException {
        String query = "SELECT * FROM UTENTE WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User getUser(int id) throws SQLException {
        String sql = "SELECT * FROM UTENTE WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("ID"), rs.getString("NOME"), rs.getString("COGNOME"), rs.getString("USERNAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));
                }
            }
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM UTENTE WHERE USERNAME = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("ID"), rs.getString("NOME"), rs.getString("COGNOME"), rs.getString("USERNAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));
                }
            }
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM UTENTE WHERE EMAIL = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("ID"), rs.getString("NOME"), rs.getString("COGNOME"), rs.getString("USERNAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));
                }
            }
        }
        return null;
    }

    // ==================================================================================
    // 3. LETTURA DATI GTFS BASE
    // ==================================================================================

    public Stop getStop(String id) throws SQLException {
        String sql = "SELECT * FROM Fermata WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Stop(rs.getString("ID"), rs.getString("CODICE"), rs.getString("NOME"), rs.getFloat("LATITUDINE"), rs.getFloat("LONGITUDINE"));
                }
            }
        }
        return null;
    }

    public ArrayList<Stop> getStops() throws SQLException {
        ArrayList<Stop> stops = new ArrayList<>();
        String sql = "SELECT * FROM Fermata";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stops.add(new Stop(rs.getString("ID"), rs.getString("CODICE"), rs.getString("NOME"), rs.getFloat("LATITUDINE"), rs.getFloat("LONGITUDINE")));
            }
        }
        return stops;
    }

    public List<Stop> getStopsByName(String name) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        String sql = "SELECT * FROM Fermata WHERE NOME LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(new Stop(rs.getString("ID"), rs.getString("CODICE"), rs.getString("NOME"), rs.getFloat("LATITUDINE"), rs.getFloat("LONGITUDINE")));
                }
            }
        }
        return stops;
    }

    /**
     * Restituisce tutte le fermate (senza duplicati) attraversate da una linea.
     */
    public List<Stop> getStopsByRoute(String routeId) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        // Seleziona le fermate distinte collegate ai viaggi di quel percorso
        String sql = "SELECT DISTINCT F.* " +
                "FROM Fermata F " +
                "INNER JOIN FERMATA_ORARIO FO ON F.ID = FO.FERMATA_ID " +
                "INNER JOIN Viaggio V ON FO.VIAGGIO_ID = V.ID " +
                "WHERE V.ID IN(SELECT ID FROM VIAGGIO WHERE PERCORSO_ID = ? AND DIREZIONE = 0 LIMIT 1 ) OR V.ID IN(SELECT ID FROM VIAGGIO WHERE PERCORSO_ID = ? AND DIREZIONE = 1 LIMIT 1)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, routeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(new Stop(
                            rs.getString("ID"),
                            rs.getString("CODICE"),
                            rs.getString("NOME"),
                            rs.getFloat("LATITUDINE"),
                            rs.getFloat("LONGITUDINE")
                    ));
                }
            }
        }
        return stops;
    }


    public List<Stop> getStopsByRouteByDirection(String routeId, int direction) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        // Seleziona le fermate distinte collegate ai viaggi di quel percorso
        String sql = "SELECT DISTINCT F.* " +
                "FROM Fermata F " +
                "INNER JOIN FERMATA_ORARIO FO ON F.ID = FO.FERMATA_ID " +
                "INNER JOIN Viaggio V ON FO.VIAGGIO_ID = V.ID " +
                "WHERE V.ID IN(SELECT ID FROM VIAGGIO WHERE PERCORSO_ID = ? AND V.DIREZIONE = ? LIMIT 1)";


        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, routeId);
            pstmt.setInt(2, direction);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(new Stop(
                            rs.getString("ID"),
                            rs.getString("CODICE"),
                            rs.getString("NOME"),
                            rs.getFloat("LATITUDINE"),
                            rs.getFloat("LONGITUDINE")
                    ));
                }
            }
        }
        return stops;
    }





    public Route getRoute(String id) throws SQLException {
        String sql = "SELECT * FROM Percorso WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Route(rs.getString("ID"), rs.getString("AGENZIA_ID"), rs.getString("NOME_BREVE"), rs.getString("NOME_COMPLETO"), rs.getString("TIPO"));
                }
            }
        }
        return null;
    }

    public List<Route> getRoutesByName(String name) throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM PERCORSO WHERE NOME_BREVE LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    routes.add(new Route(rs.getString("ID"), rs.getString("AGENZIA_ID"), rs.getString("NOME_BREVE"), rs.getString("NOME_COMPLETO"), rs.getString("TIPO")));
                }
            }
        }
        return routes;
    }

    public Trip getTrip(String id) throws SQLException {
        String sql = "SELECT * FROM Viaggio WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Trip(rs.getString("ID"), rs.getString("PERCORSO_ID"), rs.getString("SERVIZIO_ID"), rs.getString("TESTO_DESTINAZIONE"), rs.getString("NOME_BREVE"), rs.getInt("DIREZIONE"), rs.getInt("ACCESSIBILE_DIVERSAMENTE_ABILI"));
                }
            }
        }
        return null;
    }

    public ArrayList<Bus> getBusList() throws SQLException {
        ArrayList<Bus> busList = new ArrayList<>();
        String sql = "SELECT * FROM Bus ORDER BY ID";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                busList.add(new Bus(rs.getString("ID"), rs.getString("LABEL"), rs.getString("LICENSE_PLATE")));
            }
        }
        return busList;
    }

    public ArrayList<String> getIdBusList() throws SQLException {
        ArrayList<String> idList = new ArrayList<>();
        String sql = "SELECT ID FROM Bus ORDER BY ID";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                idList.add(rs.getString("ID"));
            }
        }
        return idList;
    }

    // ==================================================================================
    // 4. QUERY COMPLESSE: GESTIONE SEPARATA STATIC / DYNAMIC / SMART
    // ==================================================================================

    public List<BusInUnaFermataRecord> getArrivals(String stopId, boolean online) throws SQLException {
        if (online) {
            return getRealTimeArrivals(stopId);
        } else {
            return getStaticArrivals(stopId);
        }
    }

    public List<BusInUnaFermataRecord> getArrivalsByRoute(String stopId, String routeId, boolean online) throws SQLException {
        if (online) {
            return getRealTimeArrivalsByRoute(stopId, routeId);
        } else {
            return getStaticArrivalsByRoute(stopId, routeId);
        }
    }

    public BusInUnaFermataRecord getNextArrival(String stopId, boolean online) throws SQLException {
        List<BusInUnaFermataRecord> list = getArrivals(stopId, online);
        return list.isEmpty() ? null : list.get(0);
    }

    public BusInUnaFermataRecord getNextArrivalByRoute(String stopId, String routeId, boolean online) throws SQLException {
        List<BusInUnaFermataRecord> list = getArrivalsByRoute(stopId, routeId, online);
        return list.isEmpty() ? null : list.get(0);
    }

    // --- LOGICA STATICA ---

    public List<BusInUnaFermataRecord> getStaticArrivals(String stopId) throws SQLException {
        List<BusInUnaFermataRecord> rawList = fetchRawArrivals(stopId, null);
        return filterAndSortStatic(rawList);
    }

    public List<BusInUnaFermataRecord> getStaticArrivalsByRoute(String stopId, String routeId) throws SQLException {
        List<BusInUnaFermataRecord> rawList = fetchRawArrivals(stopId, routeId);
        return filterAndSortStatic(rawList);
    }

    private List<BusInUnaFermataRecord> filterAndSortStatic(List<BusInUnaFermataRecord> list) {
        LocalTime now = LocalTime.now();
        List<BusInUnaFermataRecord> result = new ArrayList<>();

        for (BusInUnaFermataRecord bus : list) {
            bus.setRealTime(false);
            bus.setRitardoInSecondi(0);
            LocalTime scheduled = bus.getOrarioEffettivo(); // Se ritardo è 0, ritorna lo statico
            if (scheduled.isAfter(now)) {
                result.add(bus);
            }
        }
        Collections.sort(result);
        return result;
    }

    // --- LOGICA DINAMICA ---

    public List<BusInUnaFermataRecord> getRealTimeArrivals(String stopId) throws SQLException {
        List<BusInUnaFermataRecord> rawList = fetchRawArrivals(stopId, null);
        return filterAndSortDynamic(rawList, stopId);
    }

    public List<BusInUnaFermataRecord> getRealTimeArrivalsByRoute(String stopId, String routeId) throws SQLException {
        List<BusInUnaFermataRecord> rawList = fetchRawArrivals(stopId, routeId);
        return filterAndSortDynamic(rawList, stopId);
    }

    private List<BusInUnaFermataRecord> filterAndSortDynamic(List<BusInUnaFermataRecord> list, String stopId) throws SQLException {
        LocalTime now = LocalTime.now();
        List<BusInUnaFermataRecord> result = new ArrayList<>();

        // Se non c'è internet, questo metodo cattura l'eccezione internamente e non fa nulla
        // Le mappe di ritardi rimarranno vuote (o vecchie)
        RealTimeHandler.refreshData();

        for (BusInUnaFermataRecord bus : list) {
            // 1. Prova a mettere il RealTime. Se offline, ritardo resta 0
            RealTimeHandler.applicaRealTime(bus);

            // 2. Se ritardo è 0 (o perché bus puntuale, o perché OFFLINE, o perché manca segnale GPS)
            if (bus.getDelayInSeconds() == 0) {

                // Interroghiamo lo storico locale (che funziona anche Offline!)
                double[] stats = getStatisticheStoriche(bus.getRouteId(), stopId);
                long ritardoStorico = (long) stats[0];

                // Se c'è uno storico significativo (> 60 secondi di media)
                if (Math.abs(ritardoStorico) > 60) {
                    bus.setRitardoInSecondi(ritardoStorico);
                    bus.setIsSmartPredicted(true); // Flag per indicare che è una stima storica
                }
            } else {
                // Se ritardo > 0 dal RealTimeHandler, allora è REAL TIME vero
                bus.setRealTime(true);
            }

            // Nota: getOrarioEffettivo calcola "OrarioDB + Ritardo".
            // L'orario DB (stringa) rimane intatto dentro l'oggetto.
            LocalTime effective = bus.getOrarioEffettivo();

            if (effective.isAfter(now.minusSeconds(30))) {
                result.add(bus);
            }
        }
        Collections.sort(result);
        return result;
    }


    public Optional<PosizioneTrip> getRealTimePosition(Trip trip){
        return Optional.ofNullable(RealTimeHandler.getPosizioneTrip(trip));
    }



    // --- CORE QUERY ---

    private List<BusInUnaFermataRecord> fetchRawArrivals(String stopId, String routeId) throws SQLException {
        List<BusInUnaFermataRecord> rawList = new ArrayList<>();
        String baseSql = "SELECT VIAGGIO_ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE, " +
                "Viaggio.NOME_BREVE, DIREZIONE, ORARIO_ARRIVO, ORARIO_PARTENZA " +
                "FROM (FERMATA_ORARIO INNER JOIN Fermata ON FERMATA_ID = Fermata.ID) " +
                "INNER JOIN Viaggio ON FERMATA_ORARIO.VIAGGIO_ID = Viaggio.ID " +
                "WHERE FERMATA_ID = ? " +
                "AND ORARIO_ARRIVO > time('now', '-60 minutes') ";

        String sql = (routeId == null) ? baseSql : baseSql + " AND PERCORSO_ID = ?";

        // TRY-WITH-RESOURCES: Chiude automaticamente pstmt e rs
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, stopId);
            if (routeId != null) {
                pstmt.setString(2, routeId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rawList.add(new BusInUnaFermataRecord(
                            rs.getString("VIAGGIO_ID"),
                            rs.getString("PERCORSO_ID"),
                            rs.getString("SERVIZIO_ID"),
                            rs.getString("TESTO_DESTINAZIONE"),
                            rs.getString("NOME_BREVE"),
                            rs.getInt("DIREZIONE"),
                            rs.getString("ORARIO_ARRIVO"),
                            rs.getString("ORARIO_PARTENZA")
                    ));
                }
            }
        }
        return rawList;
    }

    // ==================================================================================
    // 5. PREFERITI UTENTE
    // ==================================================================================

    public void addUserFavouriteStop(User user, Stop stop) throws SQLException {
        String sql = "INSERT INTO PREFERITI_FERMATE (UTENTE_ID, FERMATA_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, stop.getId());
            pstmt.executeUpdate();
        }
    }

    public List<Stop> getFavouriteStopsByUser(User user) throws SQLException {
        String sql = "SELECT * FROM PREFERITI_FERMATE WHERE UTENTE_ID = ?";
        List<Stop> stops = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(getStop(rs.getString("FERMATA_ID")));
                }
            }
        }
        return stops;
    }

    public void removeUserFavouriteStop(User user, Stop stop) throws SQLException {
        String sql = "DELETE FROM PREFERITI_FERMATE WHERE UTENTE_ID = ? AND FERMATA_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, stop.getId());
            pstmt.executeUpdate();
        }
    }

    public void addUserFavouriteRoute(User user, Route route) throws SQLException {
        // NOTA: Ho corretto PREFERITI_PERCORSI in PREFERITI_LINEE per coerenza con CREATE TABLE
        String sql = "INSERT INTO PREFERITI_LINEE (UTENTE_ID, PERCORSO_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, route.getId());
            pstmt.executeUpdate();
        }
    }

    public List<Route> getFavouriteRoutesByUser(User user) throws SQLException {
        // NOTA: Ho corretto PREFERITI_PERCORSI in PREFERITI_LINEE
        String sql = "SELECT * FROM PREFERITI_LINEE WHERE UTENTE_ID = ?";
        List<Route> routes = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    routes.add(getRoute(rs.getString("PERCORSO_ID")));
                }
            }
        }
        return routes;
    }

    public void removeUserFavouriteRoute(User user, Route route) throws SQLException {
        // NOTA: Ho corretto PREFERITI_PERCORSI in PREFERITI_LINEE
        String sql = "DELETE FROM PREFERITI_LINEE WHERE UTENTE_ID = ? AND PERCORSO_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, route.getId());
            pstmt.executeUpdate();
        }
    }

    // ==================================================================================
    // 6. INSERIMENTO DATI - SCRAPER
    // ==================================================================================

    public void addBus(Bus bus) throws SQLException {
        String sql = "INSERT INTO Bus (ID, LABEL, LICENSE_PLATE) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bus.getIdBus());
            pstmt.setString(2, bus.getLabelBus());
            pstmt.setString(3, bus.getLicensePlate());
            pstmt.executeUpdate();
        }
    }

    public void addStop(Stop stop) throws SQLException {
        String sql = "INSERT INTO Fermata (ID, CODICE, NOME, LATITUDINE, LONGITUDINE) VALUES (?, ?, ?, ?, ? )";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, stop.getId());
            pstmt.setString(2, stop.getCode());
            pstmt.setString(3, stop.getName());
            pstmt.setFloat(4, stop.getLatitude());
            pstmt.setFloat(5, stop.getLongitude());
            pstmt.executeUpdate();
        }
    }

    public void addRoute(Route route) throws SQLException {
        String sql = "INSERT INTO Percorso (ID, AGENZIA_ID, NOME_BREVE, NOME_COMPLETO, TIPO) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, route.getId());
            pstmt.setString(2, route.getAgencyCode());
            pstmt.setString(3, route.getShortName());
            pstmt.setString(4, route.getLongName());
            pstmt.setString(5, route.getType());
            pstmt.executeUpdate();
        }
    }

    public void addTrip(Trip trip) throws SQLException {
        String sql = "INSERT INTO Viaggio (ID, PERCORSO_ID, SERVIZIO_ID, TESTO_DESTINAZIONE, NOME_BREVE, DIREZIONE, ACCESSIBILE_DIVERSAMENTE_ABILI) VALUES (?, ?, ?, ?, ?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trip.getId());
            pstmt.setString(2, trip.getRouteId());
            pstmt.setString(3, trip.getServiceId());
            pstmt.setString(4, trip.getTripHeadsign());
            pstmt.setString(5, trip.getTripShortName());
            pstmt.setInt(6, trip.getDirection());
            pstmt.setInt(7, trip.getWheelchair_accessible());
            pstmt.executeUpdate();
        }
    }

    public void addStopTime(StopTime stopTime) throws SQLException {
        String sql = "INSERT INTO FERMATA_ORARIO (FERMATA_ID, VIAGGIO_ID, ORARIO_PARTENZA, ORARIO_ARRIVO, FERMATA_SEQUENZA, TESTO_FERMATA, SHAPE_DIST_TRAVELED) VALUES (?, ?, ?, ?, ?, ? ,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, stopTime.getStopID());
            pstmt.setString(2, stopTime.getTripID());
            pstmt.setString(3, stopTime.getDepartureTime());
            pstmt.setString(4, stopTime.getArrivalTime());
            pstmt.setInt(5, stopTime.getStopSequence());
            pstmt.setString(6, stopTime.getStopHeadsign());
            pstmt.setInt(7, stopTime.getShapeDistTraveled());
            pstmt.executeUpdate();
        }
    }


    // ==================================================================================
    // 7. Gestione qualità del servizio
    // ==================================================================================


    public void salvaOsservazioneStorica(String routeId, String stopId, int ritardo, boolean saltata) throws SQLException {
        String sql = "INSERT INTO STORICO_PERFORMANCE (ROUTE_ID, STOP_ID, RITARDO_RILEVATO, CORSA_SALTATA) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, routeId);
            pstmt.setString(2, stopId);
            pstmt.setInt(3, ritardo);
            pstmt.setInt(4, saltata ? 1 : 0);
            pstmt.executeUpdate();
        }
    }


    public double[] getStatisticheStoriche(String routeId, String stopId) throws SQLException {
        String sql = "SELECT AVG(RITARDO_RILEVATO) as media_ritardo, " +
                "AVG(CORSA_SALTATA) * 100 as percentuale_saltate " +
                "FROM STORICO_PERFORMANCE WHERE ROUTE_ID = ? AND STOP_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, routeId);
            pstmt.setString(2, stopId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new double[]{rs.getDouble("media_ritardo"), rs.getDouble("percentuale_saltate")};
            }
        }
        return new double[]{0.0, 0.0};
    }

}