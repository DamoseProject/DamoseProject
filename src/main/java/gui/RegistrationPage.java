package gui;

import model.Database;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegistrationPage implements GeneralPanel {
    private final JPanel registrationPanel;
    private JPanel topPanel;
    private JPanel centerPanel;

    private JLabel errorLabel;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private final MainFrame frame;

    public RegistrationPage(MainFrame frame) {
        this.frame = frame;
        registrationPanel = new JPanel(new BorderLayout());

        createTopPanel();
        createCenterPanel();

        registrationPanel.add(topPanel, BorderLayout.NORTH);
        registrationPanel.add(centerPanel, BorderLayout.CENTER);
    }

    // ------------------ CREAZIONE PANNELLI ------------------

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());

        BackButton backButton = new BackButton(frame);
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel registrationLabel = new JLabel("Registrati!", JLabel.CENTER);
        topPanel.add(registrationLabel, BorderLayout.CENTER);

        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);
    }

    private JPanel createFieldPanel(String labelName, JComponent field ) {
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
        JPanel usernamePanel = createFieldPanel("Username", usernameField);

        // Email
        emailField = new JTextField(20);
        JPanel emailPanel = createFieldPanel("Email", emailField);

        // Password
        passwordField = new JPasswordField(20);
        JPanel passwordPanel = createFieldPanel("Password", passwordField);

        // Conferma password
        confirmPasswordField = new JPasswordField(20);
        JPanel confirmPasswordPanel = createFieldPanel("Confirm Password", confirmPasswordField);

        // Label errore
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);

        // Bottone Registrati
        JButton registerButton = new JButton("Registrati!");
        registerButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> {
            try {
                handleRegistration();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Composizione
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



    private void handleRegistration() throws SQLException {
        RegistrationAuth auth = new RegistrationAuth(this);
        Database db = new Database();
        db.connect();
        UserAuth regAuth = new UserAuth(db);

        if (!auth.validatePresenceUsername()) {
            showError("Inserire uno username!");
        } else if (!auth.validateLengthUsername()) {
            showError("Lo username deve contenere al massimo 12 caratteri!");
        } else if (regAuth.isUsernameUnique(getUsernameRegistration())){
            showError("Username già in uso!");
        } else if (!auth.validatePresenceEmail()) {
            showError("Inserire una email per completare la registrazione!");
        } else if (!auth.validatePasswordMatch()) {
            showError("Le password non coincidono!");
        } else if (!auth.validatePasswordStrength()) {
            showError("La password deve contenere almeno: una lettera maiuscola, un numero e un carattere speciale tra: !,$,&,@,#.");
        } else if (auth.validate()) {
            try {
                Database registrationControl = new Database();
                registrationControl.connect();
                User user = new User("Davide", "Allegrini", getUsernameRegistration(), getEmailRegistration(), getPasswordRegistration());
                registrationControl.addUser(user);

                errorLabel.setVisible(false);
                frame.setView(new EmailVerificationPage(frame));
            } catch (SQLException ex) {
                showError("Errore durante la registrazione.");
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }



    @Override
    public JPanel getPanel() {
        return registrationPanel;
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