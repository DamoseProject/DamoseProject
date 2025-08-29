package gui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class BaseMapPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel mapAndResultsPanel;
    private JTextField researchField;
    private JTextArea resultsArea;
    private JXMapViewer mapViewer;

    protected BaseMapPage(MainFrame frame) {
        super(frame);

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

        JButton regLoginButton = new JButton("Accedi o Registrati!");
        regLoginButton.addActionListener(e -> frame.setView(PageFactory.createPage(PageType.LOGIN, frame)));

        JLabel mainLabel = new JLabel("Dove vuoi andare?", JLabel.CENTER);

        JButton newsButton = new JButton("News");

        
        int maxButtonWidth = Math.max(regLoginButton.getPreferredSize().width,
                newsButton.getPreferredSize().width);


        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setPreferredSize(new Dimension(maxButtonWidth + 10,
                regLoginButton.getPreferredSize().height));
        leftPanel.add(regLoginButton);


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setPreferredSize(new Dimension(maxButtonWidth + 10,
                newsButton.getPreferredSize().height));
        rightPanel.add(newsButton);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(leftPanel, gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(mainLabel, gbc);


        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
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
        JLabel errorLabel = createErrorLabel();

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
                errorLabel.setText(ErrorMessages.MISSED_RESEARCH);
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                performSearch(text);
                clearResearchField();
            }
        });
    }

    protected abstract ButtonMapPageConfig getButtonConfig();

    protected JPanel createButtonsPanel(JLabel errorLabel) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        ButtonMapPageConfig config = getButtonConfig();

        JButton addFav = new JButton("Aggiungi ai preferiti!");
        JButton checkFav = new JButton("Preferiti!");

        if (!config.isFavoritesEnabled()) {
            addFav.addActionListener(e -> {
                errorLabel.setText(config.getFavoritesErrorMessage());
                errorLabel.setVisible(true);
            });
        }

        if (!config.isViewFavoritesEnabled()) {
            checkFav.addActionListener(e -> {
                errorLabel.setText(config.getViewFavoritesErrorMessage());
                errorLabel.setVisible(true);
            });
        }

        buttonsPanel.add(addFav);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
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
        //mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    private void setupKeyboardZoom() {
        // Dispatcher globale per catturare + e - indipendentemente dal focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;

            //Più il valore dello zoom è basso, più la mappa è ingrandita
            switch (e.getKeyCode()) {
                case KeyEvent.VK_PLUS, KeyEvent.VK_EQUALS -> mapViewer.setZoom(mapViewer.getZoom() - 1); // zoom in
                case KeyEvent.VK_MINUS -> mapViewer.setZoom(mapViewer.getZoom() + 1); // zoom out
            }
            return false; // lascia che l'evento continui ad essere processato
        });
    }


    protected void setResults(String text) {
        resultsArea.setText(text);
    }


    protected String[] getResearchField() {
        String text = researchField.getText().trim();
        return text.isEmpty() ? new String[0] : text.split("\\s+");
    }

    protected void clearResearchField() {
        researchField.setText("");
    }

    protected void performSearch(String text) {
        setResults("Risultati per: " + text + "\nFermata 1\nFermata 2\nLinea A\nLinea B");
    }
}
