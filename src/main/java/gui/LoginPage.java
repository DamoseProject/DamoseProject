package gui;

import model.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginPage implements GeneralPanel {
    private final JPanel loginPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;

    private JLabel errorAccessLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private final MainFrame frame;

    public LoginPage(MainFrame frame) {
        this.frame = frame;
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        createTopPanel();
        createCenterPanel();
        createBottomPanel();

        loginPanel.add(topPanel, BorderLayout.NORTH);
        loginPanel.add(centerPanel, BorderLayout.CENTER);
        loginPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // ------------------ CREAZIONE PANNELLI ------------------

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());

        // Bottone Info a sinistra
        JButton infoButton = new JButton("Info");
        infoButton.addActionListener(e -> frame.setView(new HelpPage(frame)));
        topPanel.add(infoButton, BorderLayout.WEST);

        // Label centrata
        JLabel loginLabel = new JLabel("Login!", JLabel.CENTER);
        topPanel.add(loginLabel, BorderLayout.CENTER);

        // Strut a destra per bilanciare il bottone
        topPanel.add(Box.createHorizontalStrut(infoButton.getPreferredSize().width), BorderLayout.EAST);
    }


    private JPanel createFieldPanel(String labelName, JComponent field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelName);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        field.setMaximumSize(field.getPreferredSize());
        field.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Username
        usernameField = new JTextField(20);
        JPanel usernamePanel = createFieldPanel("Username: ", usernameField);

        // Password
        passwordField = new JPasswordField(20);
        JPanel passwordPanel = createFieldPanel("Password: ", passwordField);

        // Label errore
        errorAccessLabel = new JLabel("");
        errorAccessLabel.setForeground(Color.RED);
        errorAccessLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        errorAccessLabel.setVisible(false);

        // Bottone Accedi
        JButton accessButton = new JButton("Accedi!");
        accessButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        accessButton.addActionListener(e -> handleLogin());

        // Aggiunta al centro
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(usernamePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(accessButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(errorAccessLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    private void createBottomPanel() {
        bottomPanel = new JPanel(new BorderLayout());

        JButton guestButton = new JButton("Entra come Ospite");
        JButton registerButton = new JButton("Registrati!");
        registerButton.addActionListener(e -> frame.setView(new RegistrationPage(frame)));

        bottomPanel.add(guestButton, BorderLayout.WEST);
        bottomPanel.add(registerButton, BorderLayout.EAST);
    }

    // ------------------ LOGICA ------------------

    private void handleLogin() {
        try {
            Database db = new Database();
            db.connect();
            UserAuth loginAuth = new UserAuth(db);
            if (!loginAuth.isLoginValid(getUsernameLogin(), getPasswordLogin())) {
                errorAccessLabel.setText("Username o password errata!");
                errorAccessLabel.setVisible(true);
            } else {
                errorAccessLabel.setVisible(false);
                frame.setView(new AppPage(frame));
            }
        } catch (SQLException ex) {
            errorAccessLabel.setText("Errore di connessione al database.");
            errorAccessLabel.setVisible(true);
        }
    }



    @Override
    public JPanel getPanel() {
        return loginPanel;
    }

    public String getUsernameLogin() {
        return usernameField.getText().trim();
    }

    public String getPasswordLogin() {
        return new String(passwordField.getPassword());
    }
}