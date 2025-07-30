package gui;

import javax.swing.*;
import java.awt.*;


public class RegistrationPage implements GeneralPanel {
    private final JPanel registrationPanel;
    private final JPanel topPanel;
    private final JPanel centerPanel;
    private final JPanel usernamePanel;
    private final JPanel emailPanel;
    private final JPanel passwordPanel;
    private final JPanel confirmPasswordPanel;

    private JLabel registrationLabel;
    private JLabel username;
    private JLabel email;
    private JLabel password;
    private JLabel confirmPassword;
    private JLabel errorLabel;

    private JButton registerButton;
    private final BackButton backButton;

    private final JTextField usernameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;


    public RegistrationPage(MainFrame frame) {
        registrationPanel = new JPanel();
        registrationPanel.setLayout(new BorderLayout());


        //Top Panel
        topPanel = new JPanel();
        registrationLabel = new JLabel("Registrati!", JLabel.CENTER);
        topPanel.setLayout(new BorderLayout());
        backButton = new BackButton(frame);
        topPanel.add(backButton,  BorderLayout.WEST);
        topPanel.add(registrationLabel,  BorderLayout.CENTER);

        //Aggiungo un pannello vuoto a destra per centrare la scritta Registrati
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);




        //Center Panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));


        //Username
        username = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        username.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(usernameField.getPreferredSize());
        usernameField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        usernamePanel.add(username);
        usernamePanel.add(usernameField);


        //Email
        email = new JLabel("Email :");
        emailField = new JTextField(20);
        emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        email.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        emailField.setMaximumSize(emailField.getPreferredSize());
        emailField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        emailPanel.add(email);
        emailPanel.add(emailField);

        //Password
        password = new JLabel("Password :");
        passwordField = new JPasswordField(20);
        passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        password.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        passwordPanel.add(password);
        passwordPanel.add(passwordField);

        //Confirm Password
        confirmPassword = new JLabel("Conferma Password :");
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordPanel = new JPanel();
        confirmPasswordPanel.setLayout(new BoxLayout(confirmPasswordPanel, BoxLayout.Y_AXIS));
        confirmPassword.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        confirmPasswordField.setMaximumSize(confirmPasswordField.getPreferredSize());
        confirmPasswordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        confirmPasswordPanel.add(confirmPassword);
        confirmPasswordPanel.add(confirmPasswordField);

        //Error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);


        //Register Button
        registerButton = new JButton("Registrati!");
        registerButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> {
            Authentication auth = new Authentication(this);
            if(!auth.validatePresenceUsername()) {
                errorLabel.setText("Inserire uno username!");
                errorLabel.setVisible(true);
            } else if(!auth.validateLengthUsername()) {
                errorLabel.setText("Lo username deve contenere al massimo 12 caratteri!");
                errorLabel.setVisible(true);
            } else if(!auth.validatePresenceEmail()) {
                errorLabel.setText("Inserire una email per completare la registrazione!");
                errorLabel.setVisible(true);
            } else if(!auth.validatePasswordMatch()){
                errorLabel.setText("Le password non coincidono!");
                errorLabel.setVisible(true);
            } else if(!auth.validatePasswordStrength()) {
                errorLabel.setText("La passwword deve contenere almeno: una lettera maiuscola, un numero e un carattere speciale tra: !,$,&,@,#. ");
                errorLabel.setVisible(true);
            } else if(auth.validate()){
                errorLabel.setVisible(false);
                frame.setView(new EmailVerificationPage(frame));
            }
        });


        //Add all to the centerPanel
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


        registrationPanel.add(topPanel, BorderLayout.NORTH);
        registrationPanel.add(centerPanel, BorderLayout.CENTER);





    }

    @Override
    public JPanel getPanel() {
        return registrationPanel;
    }

    public String getUsernameField() {
        return usernameField.getText();
    }

    public String getEmailField() {
        return emailField.getText();
    }
    public char[] getPasswordField() {
        return passwordField.getPassword();
    }
    public char[] getConfirmPasswordField() {
        return confirmPasswordField.getPassword();
    }
}
