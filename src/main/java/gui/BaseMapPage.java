package gui;

import model.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public abstract class BaseMapPage extends BasePage {

    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel mapAndResultsPanel;
    private JTextField researchField;
    private JPanel resultsPanel;
    private JXMapViewer mapViewer;
    private JLabel errorLabel;
    private Database db;

    // FLAG per gestire il comportamento del JTextField
    private boolean searchConfirmed = false;
    private JPanel rowSelected = null;
    private final Map<JPanel, JPanel> expandedRows = new HashMap<>();


    protected BaseMapPage(MainFrame frame) {
        super(frame);


        this.db = new Database();
        this.db.connect();

        createTopPanel();
        createCenterPanel();
        createMapAndResultsPanel();


        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(centerPanel, BorderLayout.NORTH);
        contentPanel.add(mapAndResultsPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setupKeyboardZoom();
    }


    protected abstract ButtonMapPageConfig getButtonConfig();


    private void createTopPanel() {
        topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        ButtonMapPageConfig config = getButtonConfig();

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));


        int leftWidth = 0;
        int rightWidth = 0;
        int maxHeight = 24;


        if (config.isShowRegLoginButton()) {
            JButton regLoginButton = new JButton("Accedi o Registrati!");
            regLoginButton.addActionListener(e ->
                    frame.setView(PageFactory.createPage(PageType.LOGIN, frame))
            );
            leftPanel.add(regLoginButton);

            leftWidth = regLoginButton.getPreferredSize().width;
            maxHeight = Math.max(maxHeight, regLoginButton.getPreferredSize().height);

        } else {
            UserSession session = UserSession.getInstance();
            JButton profileButton = new JButton("👤 " + session.getUsername());

            profileButton.addActionListener(e -> {
                JPopupMenu popupMenu = createProfilePopupMenu(session);
                popupMenu.show(profileButton, 0, profileButton.getHeight());
            });

            leftPanel.add(profileButton);

            leftWidth = profileButton.getPreferredSize().width;
            maxHeight = Math.max(maxHeight, profileButton.getPreferredSize().height);
        }


        JButton newsButton = new JButton("News");
        rightPanel.add(newsButton);

        rightWidth = newsButton.getPreferredSize().width;
        maxHeight = Math.max(maxHeight, newsButton.getPreferredSize().height);


        int maxSideWidth = Math.max(leftWidth, rightWidth) + 10;
        Dimension sidePanelSize = new Dimension(maxSideWidth, maxHeight);

        leftPanel.setPreferredSize(sidePanelSize);
        rightPanel.setPreferredSize(sidePanelSize);

        
        JLabel mainLabel = new JLabel("Dove vuoi andare?", JLabel.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(leftPanel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(mainLabel, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(rightPanel, gbc);
    }


    private JPopupMenu createProfilePopupMenu(UserSession session) {
        JPopupMenu menu = new JPopupMenu();


        String userEmail = "...";
        try {
            User user = db.getUser(session.getUserId());
            if (user != null) {
                userEmail = user.getEmail();
            }
        } catch (SQLException ex) {
            userEmail = "Non disponibile";
        }


        JLabel userLabel = new JLabel("Utente: " + session.getUsername());
        JLabel emailLabel = new JLabel("Email: " + userEmail);


        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        emailLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 5, 10));


        Font infoFont = new Font("SansSerif", Font.PLAIN, 11);
        emailLabel.setFont(infoFont);
        emailLabel.setForeground(Color.GRAY);


        JMenuItem logoutItem = new JMenuItem("Esci");
        logoutItem.addActionListener(e -> {
            session.logout();
            frame.setView(PageFactory.createPage(PageType.LOGIN, frame));
        });


        menu.add(userLabel);
        menu.add(emailLabel);
        menu.add(new JSeparator());
        menu.add(logoutItem);

        return menu;
    }


    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        researchField = new JTextField(30);
        researchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, researchField.getPreferredSize().height));

        JPanel researchFieldPanel = createFieldPanel("Inserisci n. Fermata o nome della Linea: ", researchField);
        errorLabel = createErrorLabel();
        JPanel buttonPanel = createButtonPanel();

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(researchFieldPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(errorLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalStrut(10));


        researchField.addActionListener(e -> {
            String search = getResearchField();
            if (search.isEmpty()) {
                errorLabel.setText(Constants.MISSED_RESEARCH);
                errorLabel.setVisible(true);
                return;
            }
            performSearch(search);
            // Rimuove il focus dalla barra di ricerca spostandolo sulla mappa
            // in modo che i tasti + e - zoomino invece di scrivere
            mapViewer.requestFocusInWindow();
        });
    }

    private void createMapAndResultsPanel() {
        mapAndResultsPanel = new JPanel();
        mapAndResultsPanel.setLayout(new BoxLayout(mapAndResultsPanel, BoxLayout.X_AXIS));
        mapAndResultsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        mapViewer = createMapViewer();
        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.add(mapViewer, BorderLayout.CENTER);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setPreferredSize(new Dimension(450, 400));
        resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mapAndResultsPanel.add(mapContainer);
        mapAndResultsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        mapAndResultsPanel.add(resultsScroll);
    }

    protected JPanel createButtonPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        ButtonMapPageConfig config = getButtonConfig();

        JButton checkFav = new JButton("Preferiti!");

        if (!config.isViewFavoritesEnabled()) {
            checkFav.addActionListener(e -> {
                errorLabel.setText(config.getViewFavoritesErrorMessage());
                errorLabel.setVisible(true);
            });
        } else {
            checkFav.addActionListener(e -> showFavorites());
        }
        buttonsPanel.add(checkFav);
        return buttonsPanel;
    }



    protected void performSearch(String search) {
        errorLabel.setVisible(false);
        searchConfirmed = false;

        try {
            List<Stop> fermate = new ArrayList<>();
            List<Route> linee = new ArrayList<>();


            if (Character.isDigit(search.charAt(0)) && search.length() == 5) {
                Stop stop = db.getStop(search);
                if (stop != null) fermate.add(stop);
            } else {
                fermate = db.getStopsByName(search);
            }


            Route route = db.getRoute(search);
            if (route != null) linee.add(route);


            showResults(search, fermate, linee);


            searchConfirmed = true;

        } catch (SQLException ex) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(Constants.DB_SEARCH_ERROR);
            errorLabel.setVisible(true);

        }
    }

    private JXMapViewer createMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition roma = new GeoPosition(41.9028, 12.4964);
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(roma);
        mapViewer.setPreferredSize(new Dimension(500, 400));

        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    private void setupKeyboardZoom() {
        InputMap inputMap = mapViewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mapViewer.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomOut");

        actionMap.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapViewer.setZoom(mapViewer.getZoom() - 1);
            }
        });

        actionMap.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapViewer.setZoom(mapViewer.getZoom() + 1);
            }
        });

        mapViewer.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            mapViewer.setZoom(mapViewer.getZoom() - notches);
        });
    }

    private void showStopOnMap(Stop stop) {
        GeoPosition position = new GeoPosition(stop.getLatitude(), stop.getLongitude());
        mapViewer.setAddressLocation(position);
        mapViewer.setZoom(2);

        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(new DefaultWaypoint(position));

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(waypointPainter);

        mapViewer.revalidate();
        mapViewer.repaint();
    }

    private void showFavorites() {
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
            List<Stop> favorites = db.getFavouriteStopsByUser(user);

            if (favorites.isEmpty()) {
                resultsPanel.add(createGeneralRow(Constants.NO_FAVORITES_SAVED, false));
            } else {
                for (Stop stop : favorites) {
                    String text = stop.getId() + " " + stop.getName();
                    resultsPanel.add(createGeneralRow(text));
                }
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


    private void showResults(String search, List<Stop> fermate, List<Route> linee) throws SQLException {
        resultsPanel.removeAll();
        resultsPanel.add(createGeneralRow(Constants.RESULTS_HEADER + search));

        boolean foundSomething = false;

        if (!fermate.isEmpty()) {
            for (Stop fermata : fermate) {
                resultsPanel.add(createRouteRow(route));
            }
            foundSomething = true;
        }

        if (!linee.isEmpty()) {
            for (Route route : linee) {
                String text = route.getId() + " " + route.getShortName();
                resultsPanel.add(createGeneralRow(text, false));
            }
            foundSomething = true;
        }

        if (!foundSomething) {
            resultsPanel.add(createGeneralRow(Constants.NO_RESULTS, false));
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
        JButton favButton;
        try {
            stop = db.getStop(stopId);
            favButton = getFavButton(stop);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);

        rowPanel.add(leftPanel, BorderLayout.WEST);
        rowPanel.add(favButton, BorderLayout.EAST);

        arrowButton.addActionListener(e -> createSubRows(rowPanel, resultText, arrowButton));

        return rowPanel;
    }

    private void createSubRows(JPanel parentRow, String text, JButton arrowButton) {
        boolean isOpen = expandedRows.containsKey(parentRow);

        if (isOpen) {
            JPanel subList = expandedRows.remove(parentRow);
            resultsPanel.remove(subList);
            arrowButton.setText("<html>▶</html>");
        } else {
            JPanel subList = new JPanel();
            subList.setLayout(new BoxLayout(subList, BoxLayout.Y_AXIS));
            subList.setBackground(new Color(245, 245, 245));
            subList.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

            String stopId = text.split(" ")[0];

            try {
                BusInUnaFermataRecord prossimoBus = db.getProssimoArrivoInUnaFermata(stopId);

                if (prossimoBus == null) {
                    subList.add(createGeneralRow(Constants.NO_BUS_ARRIVING, false));
                } else {
                    subList.add(createBusRow(prossimoBus));
                }
            } catch (SQLException e) {
                subList.add(createGeneralRow(Constants.DATA_RETRIEVAL_ERROR, false));
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

    private JPanel createBusRow(BusInUnaFermataRecord bus) {
        String text = "🚌 " + bus.getRouteId() +
                " → " + bus.getTextDestination() +
                " | Arrivo: " + bus.getArrivalTime();

        if (bus.isRealTime()) {
            long min = bus.getRitardoInSecondi() / 60;
            text += " (+" + min + " min)";
        }

        JPanel row = createGeneralRow(text, false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return row;
    }

    protected void setResults(String text) {
        resultsPanel.removeAll();
        if (text.isEmpty()) {
            resultsPanel.revalidate(); resultsPanel.repaint();
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
        resultsPanel.revalidate(); resultsPanel.repaint();
    }

    private JPanel createRouteRow(Route route) {
        String resultText = route.getId() + " " + route.getShortName();

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


        arrowButton.addActionListener(e -> createSubRowsForRoute(rowPanel, route, arrowButton));


        JButton mapButton = new JButton("📍");
        mapButton.setPreferredSize(new Dimension(30, 25));
        mapButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mapButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        mapButton.setBorderPainted(false);
        mapButton.setContentAreaFilled(false);
        mapButton.setFocusPainted(false);



        leftPanel.add(arrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        leftPanel.add(mapButton);

        rowPanel.add(leftPanel, BorderLayout.WEST);

        JButton favButton = getFavButtonForRoute(route);
        rowPanel.add(favButton, BorderLayout.EAST);

        return rowPanel;
    }

    private void createSubRowsForRoute(JPanel parentRow, Route route, JButton arrowButton) {
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
                List<Stop> stops = db.getStopsByRoute(route);

                if (stops.isEmpty()) {
                    subList.add(createGeneralRow(Constants.NO_RESULTS, false));
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

        ButtonMapPageConfig config = getButtonConfig();

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
                    db.removeUserFavoriteRoute(user, route);
                    favButton.setText(EMPTY_STAR);
                    errorLabel.setForeground(new Color(255, 140, 0));
                    errorLabel.setText(Constants.FAV_REMOVED + route.getShortName());
                }
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(Constants.FAV_UPDATE_ERROR);
                //ex.printStackTrace();
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

        ButtonMapPageConfig config = getButtonConfig();

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
                    db.removeUserFavoriteStop(user, stop);
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
                    showStopOnMap(stop);
                    errorLabel.setVisible(false);

                    for (Component comp : resultsPanel.getComponents()) {
                        if (comp instanceof JPanel) {
                            comp.setBackground(Color.WHITE);
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



    private String getResearchField() {
        return researchField.getText().trim();
    }

    protected void clearResearchField() {
        researchField.setText("");
        searchConfirmed = false;
    }

    private int findRowPos(JPanel row) {
        Component[] components = resultsPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == row) return i;
        }
        return -1;
    }
}