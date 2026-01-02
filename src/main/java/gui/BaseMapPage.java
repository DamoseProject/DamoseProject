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

        Database db = new Database();
        db.connect();
        this.db = db;


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


        JButton regLoginButton = new JButton("Accedi o Registrati!");
        regLoginButton.addActionListener(e ->
                frame.setView(PageFactory.createPage(PageType.LOGIN, frame))
        );

        JButton newsButton = new JButton("News");

        if (config.isShowRegLoginButton()) {
            leftPanel.add(regLoginButton);
        }
        rightPanel.add(newsButton);


        int leftButtonWidth = config.isShowRegLoginButton() ? regLoginButton.getPreferredSize().width : 0;
        int rightButtonWidth = newsButton.getPreferredSize().width;
        int sideWidth = Math.max(leftButtonWidth, rightButtonWidth) + 10;
        int sideHeight = Math.max(
                config.isShowRegLoginButton() ? regLoginButton.getPreferredSize().height : 0,
                newsButton.getPreferredSize().height
        );

        Dimension sideSize = new Dimension(sideWidth, Math.max(sideHeight, 24));
        leftPanel.setPreferredSize(sideSize);
        rightPanel.setPreferredSize(sideSize);

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
                errorLabel.setText(ErrorMessages.MISSED_RESEARCH);
                errorLabel.setVisible(true);
                return;
            }
            performSearch(search);
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
            errorLabel.setText("Errore nella ricerca nel database.");
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



    private void showResults(String search, List<Stop> fermate, List<Route> linee) throws SQLException {
        resultsPanel.removeAll();
        resultsPanel.add(createGeneralRow("Risultati per: " + search));

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
                String text = route.getId() + " " + route.getShortName();
                resultsPanel.add(createGeneralRow(text, false));
            }
            foundSomething = true;
        }

        if (!foundSomething) {
            resultsPanel.add(createGeneralRow("Nessun risultato trovato", false));
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

        if (resultText.startsWith("Risultati per:")) {
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
                    subList.add(createGeneralRow("Nessun bus in arrivo", false));
                } else {
                    subList.add(createBusRow(prossimoBus));
                }
            } catch (SQLException e) {
                subList.add(createGeneralRow("Errore nel recupero dati", false));
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



    private JButton getFavButton(Stop stop) {
        final String EMPTY_STAR = "<html>&#9734;</html>";
        final String FILLED_STAR = "<html>&#9733;</html>";

        JButton favButton = new JButton(EMPTY_STAR);
        favButton.setPreferredSize(new Dimension(30, 25));
        favButton.setFont(new Font("SansSerif", Font.PLAIN, 15));

        UserSession session = UserSession.getInstance();
        ButtonMapPageConfig config = getButtonConfig();

        favButton.addActionListener(e -> {
            if (!session.isLogged()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Effettuare l’accesso per accedere ai preferiti!");
                errorLabel.setVisible(true);
                return;
            }


            if (!config.isFavoritesEnabled()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(config.getFavoritesErrorMessage());
                errorLabel.setVisible(true);
                return;
            }


            User user = null;
            try {
                user = db.getUser(session.getUserId());
                if (user == null) throw new SQLException("Utente non trovato");
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Errore nel recupero dell'utente.");
                errorLabel.setVisible(true);
                return;
            }


            try {
                if (favButton.getText().contains("9734")) {
                    db.addUserFavouriteStop(user, stop);
                    favButton.setText(FILLED_STAR);
                    errorLabel.setForeground(new Color(0, 100, 0));
                    errorLabel.setText("Aggiunto ai preferiti: " + stop.getName());
                } else {
                    db.removeUserFavoriteStop(user, stop);
                    favButton.setText(EMPTY_STAR);
                    errorLabel.setForeground(new Color(255, 140, 0));
                    errorLabel.setText("Rimosso dai preferiti: " + stop.getName());
                }
            } catch (SQLException ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Errore nell'aggiornamento dei preferiti.");

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
                    errorLabel.setText(ErrorMessages.STOP_NOT_FOUND);
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(ErrorMessages.WAYPOINT_ERROR);
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