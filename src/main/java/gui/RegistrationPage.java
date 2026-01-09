package gui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegistrationPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JLabel errorLabel;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegistrationPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        BackButton backButton = new BackButton(frame, () -> frame.setView(PageFactory.createPage(PageType.LOGIN, frame)));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel registrationLabel = new JLabel("Registrati!", JLabel.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(registrationLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        usernameField = new JTextField(20);
        JPanel usernamePanel = createFieldPanel("Username: ", usernameField);

        emailField = new JTextField(20);
        JPanel emailPanel = createFieldPanel("Email: ", emailField);

        passwordField = new JPasswordField(20);
        JPanel passwordPanel = createFieldPanel("Password: ", passwordField);

        confirmPasswordField = new JPasswordField(20);
        JPanel confirmPasswordPanel = createFieldPanel("Conferma Password: ", confirmPasswordField);

        ActionListener actionListener = e -> handleRegistration();

        usernameField.addActionListener(actionListener);
        emailField.addActionListener(actionListener);
        passwordField.addActionListener(actionListener);
        confirmPasswordField.addActionListener(actionListener);

        errorLabel = createErrorLabel();

        JButton registerButton = new JButton("Registrati!");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegistration());

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(usernamePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(emailPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(confirmPasswordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(registerButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(errorLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    private void handleRegistration() {
        RegistrationController controller = new RegistrationController();

        String userText = getUsernameRegistration();

        RegistrationResult result = controller.handleRegistration(
                userText,
                getEmailRegistration(),
                getPasswordRegistration(),
                getConfirmPasswordRegistration()
        );

        if (result.isSuccess()) {
            var db = DatabaseConnection.getInstance().getDatabase();

            if (db == null) {
                showError(Constants.CONNECTION_ERROR_DATABASE);
                return;
            }

            try {
                User newUser = db.getUserByUsername(userText);

                if (newUser != null) {
                    UserSession.getInstance().login(newUser.getId(), newUser.getUsername());
                    errorLabel.setVisible(false);
                    frame.setView(PageFactory.createPage(PageType.MAP_LOGGED, frame));
                } else {
                    showError("Registrazione avvenuta, ma errore nel login automatico.");
                }
            } catch (SQLException ex) {
                showError(Constants.CONNECTION_ERROR_DATABASE);
            }
        } else {
            showError(result.getErrorMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public String getUsernameRegistration() {
        return usernameField.getText().trim();
    }
    public String getEmailRegistration() {
        return emailField.getText().trim();
    }
    public String getPasswordRegistration() {
        return new String(passwordField.getPassword());
    }
    public String getConfirmPasswordRegistration() {
        return new String(confirmPasswordField.getPassword());
    }
}