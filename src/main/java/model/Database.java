package model;

import java.sql.*;
import java.util.ArrayList;

public class Database {


    private static String DATABASE_LINK = "jdbc:sqlite:RomeBusDatabase";
    private static Connection connection;

    public Database() {

    }


    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_LINK);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public Connection getConnection() {
        return connection;
    }





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




    /// ///////// AGGIUNGERE AGENZIA, FERMATA_ORARIO, BUS? /////////////////////////














    /// //////////////////////

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


    /// //////////////////////

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



    ///SOSTITUIRE INT CON UN ESITO PIU CHIARO
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



}
