package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public abstract class BaseMapPage extends BasePage {

    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel mapAndResultsPanel;
    private JTextField researchField;
    private JPanel resultsPanel;
    private JLabel errorLabel;
    private final Database db;

    private boolean searchConfirmed = false;

    private MapHandler mapManager;
    private ResultsHandler resultsManager;

    protected BaseMapPage(MainFrame frame) {
        super(frame);
        this.db = DatabaseConnection.getInstance().getDatabase();
        if (this.db == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Errore: connessione al database non disponibile.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        createTopPanel();
        createCenterPanel();
        createMapAndResultsPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(centerPanel, BorderLayout.NORTH);
        contentPanel.add(mapAndResultsPanel, BorderLayout.CENTER);

        mainPanel.setFocusable(true);
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                centerPanel.requestFocusInWindow();
            }
        });
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    protected abstract ButtonMapPageConfig getButtonConfig();

    private void createTopPanel() {
        topPanel = new JPanel(new GridLayout(1, 3));

        ButtonMapPageConfig config = getButtonConfig();

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        if (config.isShowRegLoginButton()) {
            JButton regLoginButton = new JButton("Accedi o Registrati!");
            regLoginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            regLoginButton.addActionListener(e ->
                    frame.setView(PageFactory.createPage(PageType.LOGIN, frame))
            );
            leftPanel.add(regLoginButton);
        } else {
            UserSession session = UserSession.getInstance();
            JButton profileButton = new JButton("👤 " + session.getUsername());
            profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            profileButton.addActionListener(e -> {
                JPopupMenu popupMenu = createProfilePopupMenu(session);
                popupMenu.show(profileButton, 0, profileButton.getHeight());
            });
            leftPanel.add(profileButton);
        }

        JLabel mainLabel = new JLabel("Dove vuoi andare?", JLabel.CENTER);
        mainLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        JButton newsButton = new JButton("\u003F\u20DD");
        newsButton.setBorderPainted(false);
        newsButton.setContentAreaFilled(false);
        newsButton.setFocusPainted(false);
        newsButton.setMargin(new Insets(0, 0, 0, 0));
        newsButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        newsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rightPanel.add(newsButton);

        topPanel.add(leftPanel);
        topPanel.add(mainLabel);
        topPanel.add(rightPanel);
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
        logoutItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            mapManager.getMapViewer().requestFocusInWindow();
        });
    }

    private void createMapAndResultsPanel() {
        mapAndResultsPanel = new JPanel();
        mapAndResultsPanel.setLayout(new BoxLayout(mapAndResultsPanel, BoxLayout.X_AXIS));
        mapAndResultsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        mapManager = new MapHandler(errorLabel);
        mapManager.setupKeyboardZoom();

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.add(mapManager.getMapViewer(), BorderLayout.CENTER);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        resultsManager = new ResultsHandler(resultsPanel, errorLabel, mapManager, getButtonConfig());

        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setPreferredSize(new Dimension(450, 400));
        resultsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultsScroll.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                resultsScroll.requestFocusInWindow();
            }
        });

        mapAndResultsPanel.add(mapContainer);
        mapAndResultsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        mapAndResultsPanel.add(resultsScroll);
    }

    protected JPanel createButtonPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        ButtonMapPageConfig config = getButtonConfig();

        JButton checkFav = new JButton("Preferiti!");
        checkFav.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (!config.isViewFavoritesEnabled()) {
            checkFav.addActionListener(e -> {
                errorLabel.setText(config.getViewFavoritesErrorMessage());
                errorLabel.setVisible(true);
            });
        } else {
            checkFav.addActionListener(e -> resultsManager.showFavorites());
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

            resultsManager.showResults(search, fermate, linee);

            searchConfirmed = true;

        } catch (SQLException ex) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(Constants.DB_SEARCH_ERROR);
            errorLabel.setVisible(true);
        }
    }

    protected void setResults(String text) {
        resultsManager.setResults(text);
    }

    protected String getResearchField() {
        return researchField.getText().trim();
    }

    protected void clearResearchField() {
        researchField.setText("");
        searchConfirmed = false;
    }
}