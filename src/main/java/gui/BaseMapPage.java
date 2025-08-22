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

public abstract class BaseMapPage extends BasePage{
    protected JPanel topPanel;
    protected JPanel centerPanel;
    protected JPanel mapAndResultsPanel;
    protected JTextField researchField;
    protected JTextArea resultsArea;

    protected BaseMapPage(MainFrame frame) {
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

        JPanel researchFieldPanel = createFieldPanel("Inserisci n. Fermata o nome della Linea!", researchField);
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        JPanel buttonsPanel = createButtonsPanel(errorLabel);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(researchFieldPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(errorLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        researchField.addActionListener(e -> {
            String text = researchField.getText().trim();
            if (text.isEmpty()) {
                errorLabel.setText("Inserire una ricerca da effettuare!");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                performSearch(text);
            }
        });
    }

    protected abstract JPanel createButtonsPanel(JLabel errorLabel);

    private void createMapAndResultsPanel() {
        mapAndResultsPanel = new JPanel();
        mapAndResultsPanel.setLayout(new BoxLayout(mapAndResultsPanel, BoxLayout.X_AXIS));

        JXMapViewer mapViewer = createMapViewer();
        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.add(mapViewer, BorderLayout.CENTER);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);

        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setPreferredSize(new Dimension(250, 400));

        mapAndResultsPanel.add(mapContainer);
        mapAndResultsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
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

        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    protected void performSearch(String text) {
        if (text.isEmpty()) {
            resultsArea.setText("Inserisci un termine di ricerca!");
            return;
        }
        String results = "Risultati per: " + text + "\nFermata 1\nFermata 2\nLinea A\nLinea B";
        resultsArea.setText(results);
    }

    public String[] getResearchField() {
        String text = researchField.getText().trim();
        return text.isEmpty() ? new String[0] : text.split("\\s+");
    }


}
