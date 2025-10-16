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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public abstract class BaseMapPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel mapAndResultsPanel;
    private JTextField researchField;
    private JPanel resultsPanel;
    private JXMapViewer mapViewer;
    private JLabel errorLabel;

    // FLAG per gestire il comportamento del JTextField
    private boolean searchConfirmed = false;


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
            String[] words = getResearchField();
            if (words.length == 0) {
                errorLabel.setText(ErrorMessages.MISSED_RESEARCH);
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                performSearch(words);
                searchConfirmed = true; // testo confermato, rimane nel campo
                researchField.getParent().requestFocusInWindow();
            }
        });

        researchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (searchConfirmed) {
                    researchField.setText("");
                    searchConfirmed = false;
                }
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
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    private JPanel createResultRow(String resultText) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowPanel.setBackground(Color.WHITE);

        JLabel resultLabel = new JLabel(resultText);
        rowPanel.add(resultLabel, BorderLayout.CENTER);

        if (!resultText.startsWith("Risultati per:")) {
            JButton addButton = getJButton(resultText);
            rowPanel.add(addButton, BorderLayout.EAST);
        } else {
            resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));
            resultLabel.setForeground(new Color(60, 60, 60));
        }

        return rowPanel;
    }

    private JButton getJButton(String resultText) {
        JButton addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(30, 25));


        ButtonMapPageConfig config = getButtonConfig();
        if (!config.isFavoritesEnabled()) {
            addButton.addActionListener(e -> {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText(config.getFavoritesErrorMessage());
                errorLabel.setVisible(true);
            });
        } else {
            addButton.addActionListener(e -> {
                errorLabel.setForeground(Color.GREEN);
                errorLabel.setText("Aggiunto ai preferiti: " + resultText);
                errorLabel.setVisible(true);
            });
        }
        return addButton;
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

    public String[] getResearchField() {
        String text = researchField.getText().trim();
        if (text.isEmpty()) {
            return new String[0];
        } else {
            String[] words = text.split("\\s+");
            return words.clone();  // restituisce una copia dell'array
        }
    }


    protected void clearResearchField() {
        researchField.setText("");
    }

    protected void performSearch(String[] words) {
        errorLabel.setVisible(false);
        String searchText = String.join(" ", words);

        setResults("Risultati per: " + searchText + "\nFermata 1\nFermata 2\nLinea A\nLinea B");
    }
}