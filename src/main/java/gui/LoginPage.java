package gui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class LoginPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JLabel errorAccessLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
    }

    private void createTopPanel() {
        topPanel = new JPanel(new GridLayout(1, 3));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel infoLabel = createClickableLabel("Q&A", Color.BLACK,
                () -> frame.setView(PageFactory.createPage(PageType.HELP, frame))
        );

        leftPanel.add(infoLabel);
        JLabel loginLabel = new JLabel("Login!", SwingConstants.CENTER);
        JPanel rightPanel = new JPanel();

        topPanel.add(leftPanel);
        topPanel.add(loginLabel);
        topPanel.add(rightPanel);
    }

    private void createCenterPanel() {
        centerPanel = createCenteredContentPanel();
        JPanel contentPanel = (JPanel) centerPanel.getComponent(0);

        usernameField = new JTextField(20);
        JPanel usernamePanel = createFieldPanel("Username: ", usernameField);

        passwordField = new JPasswordField(20);
        JPanel passwordPanel = createFieldPanel("Password: ", passwordField);
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);

        ActionListener actionListener = e -> handleLogin();
        usernameField.addActionListener(actionListener);
        passwordField.addActionListener(actionListener);

        errorAccessLabel = createErrorLabel();
        JButton accessButton = createStyledButton("Accedi!");
        accessButton.addActionListener(e -> handleLogin());

        JPanel registerPanel = createRegisterPanel();

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(usernamePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(passwordPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(accessButton);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(errorAccessLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(registerPanel);
        contentPanel.add(Box.createVerticalGlue());
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JLabel registerLabel = new JLabel("Non hai un account? Registrati ", JLabel.CENTER);

        JLabel registerButtonLabel = createClickableLabel("qui ", Color.BLUE,
                () -> frame.setView(PageFactory.createPage(PageType.REGISTRATION, frame))
        );

        JLabel registerLabel2 = new JLabel("o ", JLabel.CENTER);

        JLabel guestButtonLabel = createClickableLabel("entra come Ospite!", Color.BLUE,
                () -> frame.setView(PageFactory.createPage(PageType.MAP_GUEST, frame))
        );

        registerPanel.add(registerLabel);
        registerPanel.add(registerButtonLabel);
        registerPanel.add(registerLabel2);
        registerPanel.add(guestButtonLabel);

        return registerPanel;
    }

    private void handleLogin() {
        var db = DatabaseConnection.getInstance().getDatabase();

        if (db == null) {
            showError(errorAccessLabel, Constants.CONNECTION_ERROR_DATABASE);
            return;
        }

        try {
            UserAuth auth = new UserAuth(db);
            User user = auth.login(getUsernameLogin(), getPasswordLogin());

            if (user == null) {
                showError(errorAccessLabel, Constants.USERNAME_OR_PSW_WRONG);
            } else {
                UserSession.getInstance().login(user.getId(), user.getUsername());
                errorAccessLabel.setVisible(false);
                frame.setView(PageFactory.createPage(PageType.MAP_LOGGED, frame));
            }

        } catch (SQLException ex) {
            showError(errorAccessLabel, Constants.CONNECTION_ERROR_DATABASE);
        }
    }

    public String getUsernameLogin() {
        return usernameField.getText().trim();
    }

    public String getPasswordLogin() {
        return new String(passwordField.getPassword());
    }
}