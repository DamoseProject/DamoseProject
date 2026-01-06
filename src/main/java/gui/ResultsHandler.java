package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ResultsHandler {

    private JPanel resultsPanel;
    private Database db;
    private JLabel errorLabel;
    private MapHandler mapManager;
    private ButtonMapPageConfig config;
    private final Map<JPanel, JPanel> expandedRows = new HashMap<>();
    private JPanel rowSelected = null;

    public ResultsHandler(JPanel resultsPanel, Database db, JLabel errorLabel, MapHandler mapManager, ButtonMapPageConfig config) {
        this.resultsPanel = resultsPanel;
        this.db = db;
        this.errorLabel = errorLabel;
        this.mapManager = mapManager;
        this.config = config;
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

        if (!foundSomething) {
            resultsPanel.add(createGeneralRow(Constants.NO_RESULTS, false));
        }

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
            User user = db.getUser(session.getUserId());
            List<Stop> favStops = db.getFavouriteStopsByUser(user);
            List<Route> favRoutes = db.getFavouriteRoutesByUser(user);

            boolean hasFavorites = false;

            if (!favStops.isEmpty()) {
                for (Stop stop : favStops) {
                    if(stop == null) continue;
                    String text = stop.getId() + " " + stop.getName();
                    resultsPanel.add(createGeneralRow(text));
                }
                hasFavorites = true;
            }

            if (!favRoutes.isEmpty()) {
                for (Route route : favRoutes) {
                    JPanel rowDir0 = createRouteDirectionRow(route, 0);
                    if (rowDir0 != null) {
                        resultsPanel.add(rowDir0);
                    }

                    JPanel rowDir1 = createRouteDirectionRow(route, 1);
                    if (rowDir1 != null) {
                        resultsPanel.add(rowDir1);
                    }
                }
                hasFavorites = true;
            }

            if (!hasFavorites) {
                resultsPanel.add(createGeneralRow(Constants.NO_FAVORITES_SAVED, false));
            }

        } catch (SQLException ex) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(Constants.FAVORITES_RETRIEVAL_ERROR);
            errorLabel.setVisible(true);
        }

        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public void setResults(String text) {
        resultsPanel.removeAll();
        if (text.isEmpty()) {
            resultsPanel.revalidate();
            resultsPanel.repaint();
            return;
        }
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                JPanel resultRow = createGeneralRow(line.trim());
                resultsPanel.add(resultRow);
            }
        }
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createGeneralRow(String resultText) {
        return createGeneralRow(resultText, true);
    }

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

        String stopId = resultText.split(" ")[0];
        Stop stop;
        JButton favButton = null;
        try {
            stop = db.getStop(stopId);
            favButton = getFavButton(stop);
        } catch (SQLException e) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText("Errore dati fermata.");
            errorLabel.setVisible(true);
        }

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

    private void createSubRows(JPanel parentRow, String text, JButton arrowButton) {
        boolean isOpen = expandedRows.containsKey(parentRow);

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

            String stopId = text.split(" ")[0];

            try {
                BusInUnaFermataRecord prossimiBus = db.getNextArrival(stopId, true);

                if (prossimiBus == null) {
                    subList.add(createGeneralRow(Constants.NO_BUS_ARRIVING, false));
                } else {
                    subList.add(createBusRow(prossimiBus));
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

    private JPanel createBusRow(BusInUnaFermataRecord bus) {
        String text = "🚌 " + bus.getRouteId() +
                " → " + bus.getTextDestination() +
                " | Arrivo: " + bus.getOrarioEffettivo();

        JPanel row = createGeneralRow(text, false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return row;
    }

    private JPanel createRouteDirectionRow(Route route, int direction) {
        String directionName = String.valueOf(direction);

        try {
            List<Stop> stops = db.getStopsByRouteByDirection(route.getId(), direction);

            if (stops.isEmpty()) {
                return null;
            }

            Stop lastStop = stops.get(stops.size() - 1);
            directionName = lastStop.getName();

        } catch (SQLException e) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText("Errore nel recupero della direzione.");
            errorLabel.setVisible(true);
            return null;
        }

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
        mapButton.setBorderPainted(false);
        mapButton.setContentAreaFilled(false);
        mapButton.setFocusPainted(false);

        mapButton.addActionListener(e -> {
            try {
                mapManager.showRouteDirectionOnMap(route, direction);
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Errore visualizzazione mappa.");
                errorLabel.setVisible(true);
            }
        });

        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);

        rowPanel.add(leftPanel, BorderLayout.WEST);

        JButton favButton = getFavButtonForRoute(route);
        rowPanel.add(favButton, BorderLayout.EAST);

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
                List<Stop> stops = db.getStopsByRouteByDirection(route.getId(), direction);

                if (stops.isEmpty()) {
                    subList.add(createGeneralRow("Nessuna fermata in questa direzione", false));
                } else {
                    for (Stop stop : stops) {
                        String text = stop.getId() + " " + stop.getName();

                        JPanel stopRow = createGeneralRow(text);
                        stopRow.setBackground(new Color(250, 250, 250));
                        subList.add(stopRow);
                    }
                }

            } catch (SQLException e) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.DATA_RETRIEVAL_ERROR);
                errorLabel.setVisible(true);
                subList.add(createGeneralRow("Errore database", false));
            }

            int index = findRowPos(parentRow);
            if (index != -1) {
                resultsPanel.add(subList, index + 1);
            }

            expandedRows.put(parentRow, subList);
            arrowButton.setText("<html>▼</html>");
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JButton getFavButtonForRoute(Route route) {
        final String EMPTY_STAR = "<html>&#9734;</html>";
        final String FILLED_STAR = "<html>&#9733;</html>";

        String initialIcon = EMPTY_STAR;
        UserSession session = UserSession.getInstance();

        if (session.isLogged()) {
            try {
                User user = db.getUser(session.getUserId());
                List<Route> userFavorites = db.getFavouriteRoutesByUser(user);

                for (Route r : userFavorites) {
                    if (r.getId().equals(route.getId())) {
                        initialIcon = FILLED_STAR;
                        break;
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
                User user = db.getUser(session.getUserId());
                if (user == null) throw new SQLException(Constants.USER_NOT_FOUND);

                if (favButton.getText().contains("9734")) {
                    db.addUserFavouriteRoute(user, route);
                    favButton.setText(FILLED_STAR);
                    errorLabel.setForeground(new Color(0, 100, 0));
                    errorLabel.setText(Constants.FAV_ADDED + route.getShortName());
                } else {
                    db.removeUserFavouriteRoute(user, route);
                    favButton.setText(EMPTY_STAR);
                    errorLabel.setForeground(new Color(255, 140, 0));
                    errorLabel.setText(Constants.FAV_REMOVED + route.getShortName());
                }
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAV_UPDATE_ERROR);
            }
            errorLabel.setVisible(true);
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
                User user = db.getUser(session.getUserId());
                List<Stop> userFavorites = db.getFavouriteStopsByUser(user);

                for (Stop s : userFavorites) {
                    if(s == null) continue;
                    if (s.getId().equals(stop.getId())) {
                        initialIcon = FILLED_STAR;
                        break;
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

            User user;
            try {
                user = db.getUser(session.getUserId());
                if (user == null) throw new SQLException(Constants.USER_NOT_FOUND);
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.USER_RETRIEVAL_ERROR);
                errorLabel.setVisible(true);
                return;
            }

            try {
                if (favButton.getText().contains("9734")) {
                    db.addUserFavouriteStop(user, stop);
                    favButton.setText(FILLED_STAR);
                    errorLabel.setForeground(new Color(0, 100, 0));
                    errorLabel.setText(Constants.FAV_ADDED + stop.getName());
                } else {
                    db.removeUserFavouriteStop(user, stop);
                    favButton.setText(EMPTY_STAR);
                    errorLabel.setForeground(new Color(255, 140, 0));
                    errorLabel.setText(Constants.FAV_REMOVED + stop.getName());
                }
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAV_UPDATE_ERROR);
            }
            errorLabel.setVisible(true);
        });

        favButton.setBorderPainted(false);
        favButton.setContentAreaFilled(false);
        favButton.setFocusPainted(false);
        return favButton;
    }

    private JButton getWaypointButton(String resultText, JPanel rowPanel) {
        JButton mapButton = new JButton("📍");
        mapButton.setPreferredSize(new Dimension(30, 25));
        mapButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mapButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        mapButton.addActionListener(e -> {
            try {
                String[] parts = resultText.split(" ");
                String stopId = parts[0];
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

                } else {
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setText(Constants.STOP_NOT_FOUND);
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.WAYPOINT_ERROR);
                errorLabel.setVisible(true);
            }
        });

        mapButton.setBorderPainted(false);
        mapButton.setContentAreaFilled(false);
        mapButton.setFocusPainted(false);
        return mapButton;
    }

    private JButton getArrowButton() {
        JButton arrowButton = new JButton("<html>▶</html>");
        arrowButton.setPreferredSize(new Dimension(20, 20));
        arrowButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        arrowButton.setBorderPainted(false);
        arrowButton.setContentAreaFilled(false);
        arrowButton.setFocusPainted(false);
        return arrowButton;
    }

    private int findRowPos(JPanel row) {
        Component[] components = resultsPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == row) return i;
        }
        return -1;
    }
}
