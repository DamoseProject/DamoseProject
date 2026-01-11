package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ResultsHandler {

    private final JPanel resultsPanel;
    private final JLabel errorLabel;
    private final MapHandler mapManager;
    private final ButtonMapPageConfig config;
    private final Map<JPanel, JPanel> expandedRows = new HashMap<>();
    private JPanel rowSelected = null;
    private FavoritesManager favoritesManager;
    private BusRowFactory busRowFactory;

    public ResultsHandler(JPanel resultsPanel, JLabel errorLabel, MapHandler mapManager, ButtonMapPageConfig config) {
        this.resultsPanel = resultsPanel;
        this.resultsPanel.setLayout(new BoxLayout(this.resultsPanel, BoxLayout.Y_AXIS));
        this.errorLabel = errorLabel;
        this.mapManager = mapManager;
        this.config = config;
        Database db = getDatabase();
        this.favoritesManager = new FavoritesManager(db, errorLabel, config);
        this.busRowFactory = new BusRowFactory(db, errorLabel, mapManager);
    }

    private Database getDatabase() {
        return DatabaseConnection.getInstance().getDatabase();
    }

    private void createSubRows(JPanel parentRow, String text, JButton arrowButton, String highlightRouteId) {
        boolean isOpen = expandedRows.containsKey(parentRow);
        String stopId = text.split(" ")[0];

        if (isOpen) {
            closeSubRows(parentRow, arrowButton);
        } else {
            openSubRows(parentRow, stopId, arrowButton, highlightRouteId);
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void closeSubRows(JPanel parentRow, JButton arrowButton) {
        JPanel subList = expandedRows.remove(parentRow);
        Container parentContainer = subList.getParent();
        if (parentContainer != null) {
            parentContainer.remove(subList);
        }
        arrowButton.setText("<html>▶</html>");
    }

    private void openSubRows(JPanel parentRow, String stopId, JButton arrowButton, String highlightRouteId) {
        JPanel subList = createSubListPanel();
        populateSubList(subList, stopId, highlightRouteId);
        insertSubList(parentRow, subList);
        expandedRows.put(parentRow, subList);
        arrowButton.setText("<html>▼</html>");
    }

    private JPanel createSubListPanel() {
        JPanel subList = new JPanel();
        subList.setLayout(new BoxLayout(subList, BoxLayout.Y_AXIS));
        subList.setBackground(new Color(245, 245, 245));
        subList.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        return subList;
    }

    private void populateSubList(JPanel subList, String stopId, String highlightRouteId) {
        Database db = getDatabase();

        try {
            List<BusInUnaFermataRecord> prossimiBus = (db != null) ? db.getRealTimeArrivals(stopId) : null;
            Stop stop = (db != null) ? db.getStop(stopId) : null;

            if (prossimiBus == null || prossimiBus.isEmpty()) {
                subList.add(createGeneralRow(Constants.NO_BUS_ARRIVING, false, null));
            } else {
                addBusRowsToSubList(subList, prossimiBus, stop, highlightRouteId);
            }

        } catch (SQLException e) {
            subList.add(createGeneralRow(Constants.DATA_RETRIEVAL_ERROR, false, null));
        }
    }

    private void addBusRowsToSubList(JPanel subList, List<BusInUnaFermataRecord> prossimiBus, Stop stop, String highlightRouteId) {
        prossimiBus.sort(new BusArrivalComparator());

        HashMap<String, Integer> busCountsPerRoute = new HashMap<>();
        Set<String> addedBusInfo = new HashSet<>();

        for (BusInUnaFermataRecord prossimoBus : prossimiBus) {
            if (prossimoBus != null) {
                if (shouldAddBusRow(prossimoBus, busCountsPerRoute, addedBusInfo, highlightRouteId)) {
                    addBusRowToSubList(subList, prossimoBus, stop, highlightRouteId, busCountsPerRoute, addedBusInfo);
                }
            }
        }

        if (subList.getComponentCount() == 0) {
            subList.add(createGeneralRow(Constants.NO_BUS_ARRIVING, false, null));
        }
    }

    private boolean shouldAddBusRow(BusInUnaFermataRecord bus, HashMap<String, Integer> busCountsPerRoute,
                                    Set<String> addedBusInfo, String highlightRouteId) {
        String routeId = bus.getRouteId();
        String uniqueKey = routeId + "_" + (bus.getOrarioEffettivo() != null ? bus.getOrarioEffettivo() : bus.getOrarioStatico());

        if (addedBusInfo.contains(uniqueKey)) return false;

        int currentCount = busCountsPerRoute.getOrDefault(routeId, 0);
        return currentCount < 3;
    }

    private void addBusRowToSubList(JPanel subList, BusInUnaFermataRecord bus, Stop stop, String highlightRouteId,
                                    HashMap<String, Integer> busCountsPerRoute, Set<String> addedBusInfo) {
        String routeId = bus.getRouteId();
        boolean isHighlighted = highlightRouteId != null && highlightRouteId.equals(routeId);

        JPanel busRow = busRowFactory.createBusRow(bus, stop, isHighlighted);
        if (busRow != null) {
            subList.add(busRow);
            busCountsPerRoute.put(routeId, busCountsPerRoute.getOrDefault(routeId, 0) + 1);
            String uniqueKey = routeId + "_" + (bus.getOrarioEffettivo() != null ? bus.getOrarioEffettivo() : bus.getOrarioStatico());
            addedBusInfo.add(uniqueKey);
        }
    }

    private void insertSubList(JPanel parentRow, JPanel subList) {
        Container parentContainer = parentRow.getParent();

        if (parentContainer != null) {
            int index = -1;
            Component[] components = parentContainer.getComponents();

            for (int i = 0; i < components.length; i++) {
                if (components[i] == parentRow) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                parentContainer.add(subList, index + 1);
                parentContainer.revalidate();
                parentContainer.repaint();
            }
        }
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
                if (rowDir0 != null) {
                    resultsPanel.add(rowDir0);
                    foundSomething = true;
                }
                JPanel rowDir1 = createRouteDirectionRow(route, 1);
                if (rowDir1 != null) {
                    resultsPanel.add(rowDir1);
                    foundSomething = true;
                }
            }
        }

        if (!foundSomething) resultsPanel.add(createGeneralRow(Constants.NO_RESULTS, false, null));
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
            resultsPanel.revalidate();
            resultsPanel.repaint();
            return;
        }

        try {
            displayFavorites(session);
        } catch (SQLException ex) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(Constants.FAVORITES_RETRIEVAL_ERROR);
            errorLabel.setVisible(true);
        }
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void displayFavorites(UserSession session) throws SQLException {
        Database db = getDatabase();
        if (db == null) throw new SQLException("Connessione persa");
        User user = db.getUser(session.getUserId());
        List<Stop> favStops = db.getFavouriteStopsByUser(user);
        List<Route> favRoutes = db.getFavouriteRoutesByUser(user);

        boolean hasFavorites = false;

        if (!favStops.isEmpty()) {
            for (Stop stop : favStops) {
                if (stop == null) continue;
                resultsPanel.add(createGeneralRow(stop.getId() + " " + stop.getName()));
            }
            hasFavorites = true;
        }

        if (!favRoutes.isEmpty()) {
            hasFavorites = displayFavoriteRoutes(favRoutes) || hasFavorites;
        }

        if (!hasFavorites) resultsPanel.add(createGeneralRow(Constants.NO_FAVORITES_SAVED, false, null));
    }

    private boolean displayFavoriteRoutes(List<Route> favRoutes) {
        Set<String> processedRouteIds = new HashSet<>();
        boolean added = false;

        for (Route route : favRoutes) {
            if (processedRouteIds.contains(route.getId())) continue;
            processedRouteIds.add(route.getId());

            JPanel row0 = createRouteDirectionRow(route, 0);
            if (row0 != null) resultsPanel.add(row0);

            JPanel row1 = createRouteDirectionRow(route, 1);
            if (row1 != null) resultsPanel.add(row1);

            added = true;
        }

        return added;
    }

    public void setResults(String text) {
        resultsPanel.removeAll();
        if (!text.isEmpty()) {
            for (String line : text.split("\n")) {
                if (!line.trim().isEmpty()) resultsPanel.add(createGeneralRow(line.trim()));
            }
        }
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createGeneralRow(String resultText) {
        return createGeneralRow(resultText, true, null);
    }

    private JPanel createGeneralRow(String resultText, boolean isStopRow, String highlightRouteId) {
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

        addRowControls(rowPanel, resultText, highlightRouteId);
        return rowPanel;
    }

    private void addRowControls(JPanel rowPanel, String resultText, String highlightRouteId) {
        JButton arrowButton = UIComponentFactory.createArrowButton();
        JButton mapButton = createWaypointButton(resultText, rowPanel);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);

        rowPanel.add(leftPanel, BorderLayout.WEST);

        try {
            String stopId = resultText.split(" ")[0];
            Database db = getDatabase();
            if (db != null) {
                Stop stop = db.getStop(stopId);
                JButton favButton = favoritesManager.createFavButtonForStop(stop);
                rowPanel.add(favButton, BorderLayout.EAST);
            }
        } catch (Exception e) {
        }

        arrowButton.addActionListener(e -> createSubRows(rowPanel, resultText, arrowButton, highlightRouteId));
    }

    private JPanel createRouteDirectionRow(Route route, int direction) {
        String directionName = getDirectionName(route, direction);
        if (directionName == null) return null;

        String resultText = route.getId() + " - Direzione " + directionName;
        JPanel rowPanel = createBaseRowPanel(resultText);

        JPanel leftPanel = createRouteLeftPanel(route, direction, rowPanel);
        rowPanel.add(leftPanel, BorderLayout.WEST);

        try {
            rowPanel.add(favoritesManager.createFavButtonForRoute(route), BorderLayout.EAST);
        } catch (Exception e) {
        }

        return rowPanel;
    }

    private String getDirectionName(Route route, int direction) {
        try {
            Database db = getDatabase();
            if (db == null) return null;
            List<Stop> stops = db.getStopsByRouteByDirection(route.getId(), direction);
            if (stops == null || stops.isEmpty()) return null;
            return stops.get(stops.size() - 1).getName();
        } catch (Exception e) {
            return null;
        }
    }

    private JPanel createBaseRowPanel(String text) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowPanel.setBackground(Color.WHITE);

        JLabel resultLabel = new JLabel(text);
        rowPanel.add(resultLabel, BorderLayout.CENTER);

        return rowPanel;
    }

    private JPanel createRouteLeftPanel(Route route, int direction, JPanel rowPanel) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);

        JButton arrowButton = UIComponentFactory.createArrowButton();
        arrowButton.addActionListener(e -> createSubRowsForRouteDirection(rowPanel, route, direction, arrowButton));

        JButton mapButton = UIComponentFactory.createMapButton();
        mapButton.addActionListener(e -> {
            try {
                mapManager.showRouteDirectionOnMap(route, direction);
            } catch (Exception ex) {
            }
        });

        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);

        return leftPanel;
    }

    private void createSubRowsForRouteDirection(JPanel parentRow, Route route, int direction, JButton arrowButton) {
        boolean isOpen = expandedRows.containsKey(parentRow);
        if (isOpen) {
            closeSubRows(parentRow, arrowButton);
        } else {
            openRouteSubRows(parentRow, route, direction, arrowButton);
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void openRouteSubRows(JPanel parentRow, Route route, int direction, JButton arrowButton) {
        JPanel subList = new JPanel();
        subList.setLayout(new BoxLayout(subList, BoxLayout.Y_AXIS));
        subList.setBackground(new Color(240, 248, 255));
        subList.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        populateRouteSubList(subList, route, direction);

        int index = findRowPos(parentRow);
        if (index != -1) resultsPanel.add(subList, index + 1);
        expandedRows.put(parentRow, subList);
        arrowButton.setText("<html>▼</html>");
    }

    private void populateRouteSubList(JPanel subList, Route route, int direction) {
        try {
            Database db = getDatabase();
            if (db != null) {
                List<Stop> stops = db.getStopsByRouteByDirection(route.getId(), direction);
                if (stops.isEmpty()) {
                    subList.add(createGeneralRow("Nessuna fermata", false, null));
                } else {
                    for (Stop stop : stops) {
                        subList.add(createGeneralRow(stop.getId() + " " + stop.getName(), true, route.getId()));
                    }
                }
            }
        } catch (Exception e) {
            subList.add(createGeneralRow("Errore DB", false, null));
        }
    }

    private JButton createWaypointButton(String resultText, JPanel rowPanel) {
        JButton mapButton = UIComponentFactory.createMapButton();
        mapButton.addActionListener(e -> handleWaypointClick(resultText, rowPanel));
        return mapButton;
    }

    private void handleWaypointClick(String resultText, JPanel rowPanel) {
        try {
            String stopId = resultText.split(" ")[0];
            Database db = getDatabase();
            if (db != null) {
                Stop stop = db.getStop(stopId);
                if (stop != null) {
                    mapManager.showStopOnMap(stop);
                    errorLabel.setVisible(false);
                    resetRowHighlights();
                    rowPanel.setBackground(Color.LIGHT_GRAY);
                    rowSelected = rowPanel;
                }
            }
        } catch (Exception ex) {
            errorLabel.setText(Constants.WAYPOINT_ERROR);
        }
    }

    private void resetRowHighlights() {
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
    }

    private int findRowPos(JPanel row) {
        Component[] components = resultsPanel.getComponents();
        for (int i = 0; i < components.length; i++) if (components[i] == row) return i;
        return -1;
    }
}