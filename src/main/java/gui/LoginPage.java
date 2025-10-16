package gui;

import model.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JLabel errorAccessLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        createBottomPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        JButton infoButton = new JButton("Info");
        infoButton.addActionListener(e -> frame.setView(PageFactory.createPage(PageType.HELP, frame)));
        JLabel loginLabel = new JLabel("Login!", JLabel.CENTER);
        topPanel.add(infoButton, BorderLayout.WEST);
        topPanel.add(loginLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(infoButton.getPreferredSize().width), BorderLayout.EAST);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        usernameField = new JTextField(20);
        JPanel usernamePanel = createFieldPanel("Username: ", usernameField);

        passwordField = new JPasswordField(20);
        JPanel passwordPanel = createFieldPanel("Password: ", passwordField);

        errorAccessLabel = createErrorLabel();

        JButton accessButton = new JButton("Accedi!");
        accessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        accessButton.addActionListener(e -> handleLogin());

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
        guestButton.addActionListener(e -> frame.setView(PageFactory.createPage(PageType.MAP_GUEST, frame)));
        JButton registerButton = new JButton("Registrati!");
        registerButton.addActionListener(e -> frame.setView(PageFactory.createPage(PageType.REGISTRATION, frame)));
        bottomPanel.add(guestButton, BorderLayout.WEST);
        bottomPanel.add(registerButton, BorderLayout.EAST);
    }

    private void handleLogin() {
        try {
            Database db = new Database();
            db.connect();
            UserAuth loginAuth = new UserAuth(db);
            if (!loginAuth.isLoginValid(getUsernameLogin(), getPasswordLogin())) {
                errorAccessLabel.setText(ErrorMessages.USERNAME_OR_PSW_WRONG);
                errorAccessLabel.setVisible(true);
            } else {
                errorAccessLabel.setVisible(false);
                frame.setView(PageFactory.createPage(PageType.MAP_LOGGED, frame));
            }
        } catch (SQLException ex) {
            errorAccessLabel.setText(ErrorMessages.CONNECTION_ERROR_DATABASE);
            errorAccessLabel.setVisible(true);
        }
    }

    public String getUsernameLogin() { return usernameField.getText().trim(); }
    public String getPasswordLogin() { return new String(passwordField.getPassword()); }
}