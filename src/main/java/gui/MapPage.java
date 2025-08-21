package gui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;

public class MapPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel mapAndResultsPanel;
    private JTextField researchField;
    private JTextArea resultsArea;

    public MapPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        createMapAndResultsPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(mapAndResultsPanel, BorderLayout.SOUTH);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());

        JButton regLoginButton = new JButton("Accedi o Registrati!");
        regLoginButton.addActionListener(e -> frame.setView(new LoginPage(frame)));

        JLabel mainLabel = new JLabel("Dove vuoi andare?", JLabel.CENTER);

        JButton newsButton = new JButton("News");

        int strutWidth = regLoginButton.getPreferredSize().width - newsButton.getPreferredSize().width;
        Component horizontalStrut = Box.createHorizontalStrut(Math.max(strutWidth, 0));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(mainLabel, BorderLayout.CENTER);
        centerPanel.add(horizontalStrut, BorderLayout.EAST);

        topPanel.add(regLoginButton, BorderLayout.WEST);
        topPanel.add(newsButton, BorderLayout.EAST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        researchField = new JTextField(30);
        researchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, researchField.getPreferredSize().height));

        // Creiamo il pannello con la label sopra il JTextField
        JPanel researchFieldPanel = createFieldPanel("Inserisci n. Fermata o nome della Linea!", researchField);

        // Label di errore invisibile
        JLabel errorLabel = new JLabel("Inserire una ricerca da effettuare");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        // Bottoni
        JButton addFav = new JButton("Aggiungi ai preferiti!");
        JButton checkFav = new JButton("Preferiti!");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        addFav.setAlignmentY(Component.CENTER_ALIGNMENT);
        checkFav.setAlignmentY(Component.CENTER_ALIGNMENT);

        buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsPanel.add(addFav);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonsPanel.add(checkFav);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inseriamo tutto nel centerPanel con spazi verticali
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(researchFieldPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(errorLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // ActionListener per Invio
        researchField.addActionListener(e -> {
            String text = researchField.getText().trim();
            if (text.isEmpty()) {
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                performSearch(text); 
            }
        });
    }


    private void createMapAndResultsPanel() {
        mapAndResultsPanel = new JPanel();
        mapAndResultsPanel.setLayout(new BoxLayout(mapAndResultsPanel, BoxLayout.X_AXIS));

        // Mappa
        JXMapViewer mapViewer = createMapViewer();
        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.add(mapViewer, BorderLayout.CENTER);

        // Area risultati
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);

        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setPreferredSize(new Dimension(250, 400)); // larghezza fissa, altezza come la mappa

        mapAndResultsPanel.add(mapContainer);
        mapAndResultsPanel.add(Box.createRigidArea(new Dimension(10, 0))); // spazio tra mappa e risultati
        mapAndResultsPanel.add(resultsScroll);
    }

    private JXMapViewer createMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition roma = new GeoPosition(41.9028, 12.4964);
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(roma);

        mapViewer.setPreferredSize(new Dimension(500, 400));

        // listener
        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    private void performSearch(String text) {
        text = researchField.getText().trim();
        if(text.isEmpty()) {
            resultsArea.setText("Inserisci un termine di ricerca!");
            return;
        }

        // Simulazione risultati
        String results = "Risultati per: " + text + "\n";
        results += "Fermata 1\nFermata 2\nLinea A\nLinea B"; // esempio

        resultsArea.setText(results);
    }

    public String getResearchField() {
        return researchField.getText().trim();
    }
}
