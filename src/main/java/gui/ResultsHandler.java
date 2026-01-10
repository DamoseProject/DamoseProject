package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.List;


public class ResultsHandler {

    private final JPanel resultsPanel;
    private final JLabel errorLabel;
    private final MapHandler mapManager;
    private final ButtonMapPageConfig config;
    private final Map<JPanel, JPanel> expandedRows = new HashMap<>();
    private JPanel rowSelected = null;

    public ResultsHandler(JPanel resultsPanel, JLabel errorLabel, MapHandler mapManager, ButtonMapPageConfig config) {
        this.resultsPanel = resultsPanel;
        this.errorLabel = errorLabel;
        this.mapManager = mapManager;
        this.config = config;
    }

    private Database getDatabase() {
        return DatabaseConnection.getInstance().getDatabase();
    }

    private void createSubRows(JPanel parentRow, String text, JButton arrowButton) {
        boolean isOpen = expandedRows.containsKey(parentRow);
        String stopId = text.split(" ")[0];

        Database db = getDatabase();

        if (isOpen) {
            JPanel subList = expandedRows.remove(parentRow);
            Container parentContainer = subList.getParent();
            if (parentContainer != null) {
                parentContainer.remove(subList);
            }
            arrowButton.setText("<html>▶</html>");

        } else {
            JPanel subList = new JPanel();
            subList.setLayout(new BoxLayout(subList, BoxLayout.Y_AXIS));
            subList.setBackground(new Color(245, 245, 245));
            subList.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

            try {
                List<BusInUnaFermataRecord> prossimiBus = (db != null) ? db.getRealTimeArrivals(stopId) : null;
                Stop stop = (db != null) ? db.getStop(stopId) : null;

                if (prossimiBus == null || prossimiBus.isEmpty()) {
                    subList.add(createGeneralRow(Constants.NO_BUS_ARRIVING, false));
                } else {
                    prossimiBus.sort((b1, b2) -> {
                        int p1 = b1.isRealTime() ? 1 : (b1.getIsSmartPredicted() ? 2 : 3);
                        int p2 = b2.isRealTime() ? 1 : (b2.getIsSmartPredicted() ? 2 : 3);

                        if (p1 != p2) {
                            return Integer.compare(p1, p2);
                        } else {
                            LocalTime t1 = (b1.getOrarioEffettivo() != null) ? b1.getOrarioEffettivo() : b1.getOrarioStatico();
                            LocalTime t2 = (b2.getOrarioEffettivo() != null) ? b2.getOrarioEffettivo() : b2.getOrarioStatico();
                            return t1.compareTo(t2);
                        }
                    });

                    HashMap<String, Integer> busCountsPerRoute = new HashMap<>();
                    Set<String> addedBusInfo = new HashSet<>();
                    for (BusInUnaFermataRecord prossimoBus : prossimiBus) {
                        if (prossimoBus != null) {
                            String routeId = prossimoBus.getRouteId();

                            LocalTime timeToCheck = prossimoBus.getOrarioEffettivo();
                            if (timeToCheck == null) {
                                timeToCheck = prossimoBus.getOrarioStatico();
                            }
                            String uniqueKey = routeId + "_" + timeToCheck;

                            if(addedBusInfo.contains(uniqueKey)) {
                                continue;
                            }

                            int currentCount = busCountsPerRoute.getOrDefault(routeId, 0);
                            if (currentCount < 3) {
                                JPanel busRow = createBusRow(prossimoBus, stop);
                                if (busRow != null) {
                                    subList.add(busRow);
                                    busCountsPerRoute.put(routeId, currentCount + 1);
                                    addedBusInfo.add(uniqueKey);
                                }
                            }
                        }
                    }
                    if (subList.getComponentCount() == 0) {
                        subList.add(createGeneralRow(Constants.NO_BUS_ARRIVING, false));
                    }
                }

            } catch (SQLException e) {
                subList.add(createGeneralRow(Constants.DATA_RETRIEVAL_ERROR, false));
            }

            Container parentContainer = parentRow.getParent();
            int index = -1;

            if (parentContainer != null) {
                Component[] components = parentContainer.getComponents();
                for (int i = 0; i < components.length; i++) {
                    if (components[i] == parentRow) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    parentContainer.add(subList, index + 1);
                }
            }

            expandedRows.put(parentRow, subList);
            arrowButton.setText("<html>▼</html>");
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }


    private JPanel createBusRow(BusInUnaFermataRecord bus, Stop currentStop) {
        JPanel rowPanel = new JPanel(new BorderLayout());

        Color normalColor = new Color(240, 240, 240);
        Color hoverColor = new Color(225, 225, 225);

        rowPanel.setBackground(normalColor);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));

        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));



        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        String infoText = "";

        if (bus.isRealTime()) {
            LocalTime orarioEffettivo = bus.getOrarioEffettivo();
            LocalTime now = LocalTime.now();
            long timeRemaining = java.time.temporal.ChronoUnit.MINUTES.between(now, orarioEffettivo);
            if (timeRemaining < 0) {
                return null;
            }

            String labelGrigia;
            String valoreVerde;

            if (timeRemaining == 0) {
                labelGrigia = "In arrivo: ";
                valoreVerde = "Ora";
            } else {
                labelGrigia = "Arrivo: ";
                valoreVerde = timeRemaining + " min";
            }
            infoText = "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                    " <span style='color:gray'>| " + labelGrigia + "</span>" +
                    "<span style='color:green'><b>" + valoreVerde + "</b></span>" +
                    "</nobr></html>";

        } else if (bus.getIsSmartPredicted()) {
            LocalTime orarioPredetto = bus.getOrarioEffettivo();
            LocalTime orarioProgrammato = bus.getOrarioStatico();
            infoText = "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                    " <span style='color:gray'>| Arrivo programmato: " + orarioProgrammato.format(timeFormatter) + "</span>" +
                    " <span style='color:#0044CC'>| Previsione: " + orarioPredetto.format(timeFormatter) + "</span>" +
                    "</nobr></html>";

        } else {
            LocalTime orarioStatico = bus.getOrarioStatico();
            infoText = "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                    " <span style='color:gray'>| Arrivo programmato: " + orarioStatico.format(timeFormatter) + "</span>" +
                    "</nobr></html>";
        }

        JLabel label = new JLabel(infoText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(50, 50, 50));

        rowPanel.add(label, BorderLayout.CENTER);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Database db = getDatabase();
                    if (db == null) return;

                    int direction = 1;
                    List<Stop> stopsDir0 = db.getStopsByRouteByDirection(bus.getRouteId(), 0);

                    if (stopsDir0 != null) {
                        for (Stop s : stopsDir0) {
                            if (s.getId().equals(currentStop.getId())) {
                                direction = 0;
                                break;
                            }
                        }
                    }

                    mapManager.showRouteWithBus(bus.getRouteId(), direction, bus, currentStop);

                } catch (Exception ex) {
                    errorLabel.setText("Errore caricamento percorso.");
                    errorLabel.setVisible(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                rowPanel.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                rowPanel.setBackground(normalColor);
            }
        };

        rowPanel.addMouseListener(clickListener);
        label.addMouseListener(clickListener);

        return rowPanel;
    }


    public void showResults(String search, List<Stop> fermate, List<Route> linee) throws SQLException {
        resultsPanel.removeAll();
        resultsPanel.add(createGeneralRow(Constants.RESULTS_HEADER + search));
        boolean foundSomething = false;

        if (!fermate.isEmpty()) {
            for (Stop fermata : fermate) {
                String text = fermata.getId() + " " + fermata.getName();
                resultsPanel.add(createGeneralRow(text));
            }
            foundSomething = true;
        }

        if (!linee.isEmpty()) {
            for (Route route : linee) {
                JPanel rowDir0 = createRouteDirectionRow(route, 0);
                if (rowDir0 != null) { resultsPanel.add(rowDir0); foundSomething = true; }
                JPanel rowDir1 = createRouteDirectionRow(route, 1);
                if (rowDir1 != null) { resultsPanel.add(rowDir1); foundSomething = true; }
            }
        }

        if (!foundSomething) resultsPanel.add(createGeneralRow(Constants.NO_RESULTS, false));
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public void showFavorites() {
        errorLabel.setVisible(false);
        resultsPanel.removeAll();
        resultsPanel.add(createGeneralRow(Constants.FAVORITES_HEADER));
        UserSession session = UserSession.getInstance();

        if (!session.isLogged()) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(Constants.LOGIN_REQUIRED_LIST);
            errorLabel.setVisible(true);
            resultsPanel.revalidate(); resultsPanel.repaint(); return;
        }

        try {
            Database db = getDatabase();
            if (db == null) throw new SQLException("Connessione persa");
            User user = db.getUser(session.getUserId());
            List<Stop> favStops = db.getFavouriteStopsByUser(user);
            List<Route> favRoutes = db.getFavouriteRoutesByUser(user);

            boolean hasFavorites = false;
            if (!favStops.isEmpty()) {
                for (Stop stop : favStops) {
                    if(stop == null) continue;
                    resultsPanel.add(createGeneralRow(stop.getId() + " " + stop.getName()));
                }
                hasFavorites = true;
            }
            if (!favRoutes.isEmpty()) {
                for (Route route : favRoutes) {
                    JPanel row0 = createRouteDirectionRow(route, 0); if(row0!=null) resultsPanel.add(row0);
                    JPanel row1 = createRouteDirectionRow(route, 1); if(row1!=null) resultsPanel.add(row1);
                }
                hasFavorites = true;
            }
            if (!hasFavorites) resultsPanel.add(createGeneralRow(Constants.NO_FAVORITES_SAVED, false));
        } catch (SQLException ex) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(Constants.FAVORITES_RETRIEVAL_ERROR);
            errorLabel.setVisible(true);
        }
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate(); resultsPanel.repaint();
    }

    public void setResults(String text) {
        resultsPanel.removeAll();
        if (!text.isEmpty()) {
            for (String line : text.split("\n")) {
                if (!line.trim().isEmpty()) resultsPanel.add(createGeneralRow(line.trim()));
            }
        }
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate(); resultsPanel.repaint();
    }

    private JPanel createGeneralRow(String resultText) { return createGeneralRow(resultText, true); }


    private JPanel createGeneralRow(String resultText, boolean isStopRow) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowPanel.setBackground(Color.WHITE);

        JLabel resultLabel = new JLabel(resultText);
        rowPanel.add(resultLabel, BorderLayout.CENTER);

        if (resultText.startsWith(Constants.RESULTS_HEADER) || resultText.equals(Constants.FAVORITES_HEADER)) {
            resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));
            resultLabel.setForeground(new Color(60, 60, 60));
            return rowPanel;
        }

        if (!isStopRow) {
            resultLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            resultLabel.setForeground(new Color(80, 80, 80));
            return rowPanel;
        }

        JButton arrowButton = getArrowButton();
        JButton mapButton = getWaypointButton(resultText, rowPanel);

        Stop stop = null;
        JButton favButton = null;
        try {
            String stopId = resultText.split(" ")[0];
            Database db = getDatabase();
            if(db != null) {
                stop = db.getStop(stopId);
                favButton = getFavButton(stop);
            }
        } catch (Exception e) {}

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);

        rowPanel.add(leftPanel, BorderLayout.WEST);


        if (favButton != null) {
            rowPanel.add(favButton, BorderLayout.EAST);
        }

        arrowButton.addActionListener(e -> createSubRows(rowPanel, resultText, arrowButton));
        return rowPanel;
    }

    private JPanel createRouteDirectionRow(Route route, int direction) {
        String directionName = String.valueOf(direction);
        try {
            Database db = getDatabase();
            if (db == null) return null;
            List<Stop> stops = db.getStopsByRouteByDirection(route.getId(), direction);
            if (stops == null || stops.isEmpty()) return null;
            directionName = stops.get(stops.size() - 1).getName();
        } catch (Exception e) { return null; }

        String resultText = route.getId() + " - Direzione " + directionName;
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowPanel.setBackground(Color.WHITE);

        JLabel resultLabel = new JLabel(resultText);
        rowPanel.add(resultLabel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);

        JButton arrowButton = getArrowButton();
        arrowButton.addActionListener(e -> createSubRowsForRouteDirection(rowPanel, route, direction, arrowButton));

        JButton mapButton = new JButton("📍");
        mapButton.setPreferredSize(new Dimension(30, 25));
        mapButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mapButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        mapButton.setContentAreaFilled(false);
        mapButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mapButton.addActionListener(e -> {
            try { mapManager.showRouteDirectionOnMap(route, direction); } catch (Exception ex) {}
        });

        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);
        rowPanel.add(leftPanel, BorderLayout.WEST);

        try { rowPanel.add(getFavButtonForRoute(route), BorderLayout.EAST); } catch(Exception e){}

        return rowPanel;
    }

    private void createSubRowsForRouteDirection(JPanel parentRow, Route route, int direction, JButton arrowButton) {
        boolean isOpen = expandedRows.containsKey(parentRow);
        if (isOpen) {
            JPanel subList = expandedRows.remove(parentRow);
            resultsPanel.remove(subList);
            arrowButton.setText("<html>▶</html>");
        } else {
            JPanel subList = new JPanel();
            subList.setLayout(new BoxLayout(subList, BoxLayout.Y_AXIS));
            subList.setBackground(new Color(240, 248, 255));
            subList.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
            try {
                Database db = getDatabase();
                if (db != null) {
                    List<Stop> stops = db.getStopsByRouteByDirection(route.getId(), direction);
                    if (stops.isEmpty()) subList.add(createGeneralRow("Nessuna fermata", false));
                    else for (Stop stop : stops) subList.add(createGeneralRow(stop.getId() + " " + stop.getName()));
                }
            } catch (Exception e) { subList.add(createGeneralRow("Errore DB", false)); }

            int index = findRowPos(parentRow);
            if (index != -1) resultsPanel.add(subList, index + 1);
            expandedRows.put(parentRow, subList);
            arrowButton.setText("<html>▼</html>");
        }
        resultsPanel.revalidate(); resultsPanel.repaint();
    }


    private JButton getFavButtonForRoute(Route route) {
        final String EMPTY_STAR = "<html>&#9734;</html>";
        final String FILLED_STAR = "<html>&#9733;</html>";
        String initialIcon = EMPTY_STAR;
        UserSession session = UserSession.getInstance();

        if (session.isLogged()) {
            try {
                Database db = getDatabase();
                if (db != null) {
                    User user = db.getUser(session.getUserId());
                    List<Route> userFavorites = db.getFavouriteRoutesByUser(user);
                    for (Route r : userFavorites) {
                        if (r.getId().equals(route.getId())) {
                            initialIcon = FILLED_STAR;
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAVORITES_RETRIEVAL_ERROR);
                errorLabel.setVisible(true);
            }
        }

        JButton favButton = new JButton(initialIcon);
        favButton.setPreferredSize(new Dimension(30, 25));
        favButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        favButton.setBorderPainted(false);
        favButton.setContentAreaFilled(false);
        favButton.setFocusPainted(false);
        favButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        favButton.addActionListener(e -> {
            if (!session.isLogged()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.LOGIN_REQUIRED_FAVORITES);
                errorLabel.setVisible(true);
                return;
            }

            if (!config.isFavoritesEnabled()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(config.getFavoritesErrorMessage());
                errorLabel.setVisible(true);
                return;
            }

            try {
                Database db = getDatabase();
                if (db == null) throw new SQLException("Connessione non trovata.");
                User user = db.getUser(session.getUserId());
                if (user == null) throw new SQLException(Constants.USER_NOT_FOUND);

                if (favButton.getText().contains("9734")) {
                    db.addUserFavouriteRoute(user, route);
                    favButton.setText(FILLED_STAR);
                    errorLabel.setForeground(new Color(0, 100, 0));
                    errorLabel.setText(Constants.FAV_ADDED + route.getShortName());
                    errorLabel.setVisible(true);
                } else {
                    db.removeUserFavouriteRoute(user, route);
                    favButton.setText(EMPTY_STAR);
                    errorLabel.setForeground(new Color(255, 140, 0));
                    errorLabel.setText(Constants.FAV_REMOVED + route.getShortName());
                    errorLabel.setVisible(true);
                }
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAV_UPDATE_ERROR);
                errorLabel.setVisible(true);
            }
        });

        return favButton;
    }

    private JButton getFavButton(Stop stop) {
        final String EMPTY_STAR = "<html>&#9734;</html>";
        final String FILLED_STAR = "<html>&#9733;</html>";
        String initialIcon = EMPTY_STAR;
        UserSession session = UserSession.getInstance();

        if (session.isLogged()) {
            try {
                Database db = getDatabase();
                if (db != null) {
                    User user = db.getUser(session.getUserId());
                    List<Stop> userFavorites = db.getFavouriteStopsByUser(user);
                    for (Stop s : userFavorites) {
                        if(s == null) continue;
                        if (s.getId().equals(stop.getId())) {
                            initialIcon = FILLED_STAR;
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAVORITES_RETRIEVAL_ERROR);
                errorLabel.setVisible(true);
            }
        }

        JButton favButton = new JButton(initialIcon);
        favButton.setPreferredSize(new Dimension(30, 25));
        favButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        favButton.setBorderPainted(false);
        favButton.setContentAreaFilled(false);
        favButton.setFocusPainted(false);
        favButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        favButton.addActionListener(e -> {
            if (!session.isLogged()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.LOGIN_REQUIRED_FAVORITES);
                errorLabel.setVisible(true);
                return;
            }

            if (!config.isFavoritesEnabled()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(config.getFavoritesErrorMessage());
                errorLabel.setVisible(true);
                return;
            }

            try {
                Database db = getDatabase();
                if (db == null) throw new SQLException("Connessione persa");
                User user = db.getUser(session.getUserId());
                if (user == null) throw new SQLException(Constants.USER_NOT_FOUND);

                if (favButton.getText().contains("9734")) {
                    db.addUserFavouriteStop(user, stop);
                    favButton.setText(FILLED_STAR);
                    errorLabel.setForeground(new Color(0, 100, 0));
                    errorLabel.setText(Constants.FAV_ADDED + stop.getName());
                    errorLabel.setVisible(true);
                } else {
                    db.removeUserFavouriteStop(user, stop);
                    favButton.setText(EMPTY_STAR);
                    errorLabel.setForeground(new Color(255, 140, 0));
                    errorLabel.setText(Constants.FAV_REMOVED + stop.getName());
                    errorLabel.setVisible(true);
                }
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAV_UPDATE_ERROR);
                errorLabel.setVisible(true);
            }
        });

        return favButton;
    }

    private JButton getWaypointButton(String resultText, JPanel rowPanel) {
        JButton mapButton = new JButton("📍");
        mapButton.setPreferredSize(new Dimension(30, 25));
        mapButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mapButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        mapButton.setContentAreaFilled(false);
        mapButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mapButton.addActionListener(e -> {
            try {
                String stopId = resultText.split(" ")[0];
                Database db = getDatabase();
                if(db != null) {
                    Stop stop = db.getStop(stopId);
                    if (stop != null) {
                        mapManager.showStopOnMap(stop);
                        errorLabel.setVisible(false);

                        for (Component comp : resultsPanel.getComponents()) {
                            if (comp instanceof JPanel && !expandedRows.containsValue(comp)) {
                                comp.setBackground(Color.WHITE);
                            }
                        }
                        for (JPanel subList : expandedRows.values()) {
                            subList.setBackground(new Color(240, 248, 255));
                            for (Component subRow : subList.getComponents()) {
                                if (subRow instanceof JPanel) {
                                    subRow.setBackground(new Color(250, 250, 250));
                                }
                            }
                        }
                        rowPanel.setBackground(Color.LIGHT_GRAY);
                        rowSelected = rowPanel;
                    }
                }
            } catch (Exception ex) {
                errorLabel.setText(Constants.WAYPOINT_ERROR);
            }
        });
        mapButton.setFocusPainted(false);
        return mapButton;
    }

    private JButton getArrowButton() {
        JButton arrowButton = new JButton("<html>▶</html>");
        arrowButton.setPreferredSize(new Dimension(20, 20));
        arrowButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        arrowButton.setContentAreaFilled(false);
        arrowButton.setBorderPainted(false);
        arrowButton.setFocusPainted(false);
        arrowButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return arrowButton;
    }

    private int findRowPos(JPanel row) {
        Component[] components = resultsPanel.getComponents();
        for (int i = 0; i < components.length; i++) if (components[i] == row) return i;
        return -1;
    }
}