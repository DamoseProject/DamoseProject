package gui;

import model.Database;
import model.Stop;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    protected BaseMapPage(MainFrame frame) {
        super(frame);

        Database db = new Database();
        db.connect();

        this.db = db;


        createTopPanel();
        createCenterPanel();
        createMapAndResultsPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(mapAndResultsPanel, BorderLayout.SOUTH);

        setupKeyboardZoom();
    }

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


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(leftPanel, gbc);


        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(mainLabel, gbc);


        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
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

        // Gestione Invio
        researchField.addActionListener(e -> {
            String search = getResearchField();

            List<Stop> fermate = null;
            try {
                fermate = db.getStopsByName(search);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            for(var fermata:fermate){
                System.out.println(fermata.getName());
            }
            if (search.isEmpty()) {
                errorLabel.setText(ErrorMessages.MISSED_RESEARCH);
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                try {
                    performSearch(search);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                searchConfirmed = true; // testo confermato, rimane nel campo
                researchField.getParent().requestFocusInWindow();
            }
        });

    }

    protected abstract ButtonMapPageConfig getButtonConfig();

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
        resultsScroll.setPreferredSize(new Dimension(250, 400));
        resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mapAndResultsPanel.add(mapContainer);
        mapAndResultsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        mapAndResultsPanel.add(resultsScroll);
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

    private JPanel createResultRow(String resultText) {
        JPanel rowsPanel = new JPanel(new BorderLayout());
        rowsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rowsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowsPanel.setBackground(Color.WHITE);

        JLabel resultLabel = new JLabel(resultText);
        rowsPanel.add(resultLabel, BorderLayout.CENTER);

        if (!resultText.startsWith("Risultati per:")) {
            JButton addButton = getFavButton(resultText);
            JButton mapButton = getMapButton(resultText, rowsPanel);
            rowsPanel.add(mapButton, BorderLayout.WEST);
            rowsPanel.add(addButton, BorderLayout.EAST);
        } else {
            resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));
            resultLabel.setForeground(new Color(60, 60, 60));
        }

        return rowsPanel;
    }

    private JButton getFavButton(String resultText) {
        JButton favButton = new JButton("<html>&#9734;</html>"); // stella vuota
        favButton.setPreferredSize(new Dimension(30, 25));
        favButton.setFont(new Font("SansSerif", Font.PLAIN, 15));

        ButtonMapPageConfig config = getButtonConfig();

        favButton.addActionListener(e -> {
            if (!config.isFavoritesEnabled()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(config.getFavoritesErrorMessage());
                errorLabel.setVisible(true);
            } else {

                if (favButton.getText().equals("<html>&#9734;</html>")) {
                    favButton.setText("<html>&#9733;</html>"); // piena
                    errorLabel.setForeground(new Color(0, 100, 0)); //verde scuro
                    errorLabel.setText("Aggiunto ai preferiti: " + resultText);
                } else {
                    favButton.setText("<html>&#9734;</html>"); // vuota
                    errorLabel.setForeground(new Color(255, 140, 0)); //arancione scuro
                    errorLabel.setText("Rimosso dai preferiti: " + resultText);
                }
                errorLabel.setVisible(true);
            }
        });

        favButton.setBorderPainted(false);
        favButton.setContentAreaFilled(false);
        favButton.setFocusPainted(false);

        return favButton;
    }

    private JButton getMapButton(String resultText, JPanel rowPanel) {
        JButton mapButton = new JButton("🗺️");
        mapButton.setPreferredSize(new Dimension(30, 25));
        mapButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
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
                    errorLabel.setText("Fermata non trovata nel database.");
                    errorLabel.setVisible(true);
                }

            } catch (Exception ex) {
                //ex.printStackTrace();
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Errore durante la visualizzazione della mappa.");
                errorLabel.setVisible(true);
            }


        });

        mapButton.setBorderPainted(false);
        mapButton.setContentAreaFilled(false);
        mapButton.setFocusPainted(false);

        return mapButton;
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


    private void setupKeyboardZoom() {
        InputMap inputMap = mapViewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mapViewer.getActionMap();

        // Zoom in con +
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "zoomIn");

        // Zoom out con -
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


    protected void setResults(String text) {
        resultsPanel.removeAll();

        if (text.isEmpty()) {
            resultsPanel.revalidate();
            resultsPanel.repaint();
            return;
        }


        String[] lines = text.split("\n");

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                JPanel resultRow = createResultRow(line.trim());
                resultsPanel.add(resultRow);
            }
        }

        // Aggiungo uno spazio flessibile alla fine per spingere i risultati in alto
        resultsPanel.add(Box.createVerticalGlue());

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private String getResearchField() {
        String text = researchField.getText().trim();
        return text;

    }


    protected void clearResearchField() {
        researchField.setText("");
    }



    protected void performSearch(String search) throws SQLException {
        errorLabel.setVisible(false);



        List<Stop> fermate = db.getStopsByName(search);

        String fermateRecord = "";
        for(var fermata:fermate){
            fermateRecord += fermata.getId() + " " + fermata.getName() + "\n";
        }


        setResults("Risultati per: " + search + "\n" + fermateRecord);
    }
}