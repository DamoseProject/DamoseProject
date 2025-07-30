package gui;

import javax.swing.*;
import java.awt.*;



public class LoginPage implements GeneralPanel {
    private final JPanel loginPanel;
    private final JPanel topPanel;
    private final JPanel centerPanel;
    private final JPanel emailPanel;
    private final JPanel passwordPanel;
    private final JPanel bottomPanel;

    private JLabel loginLabel;
    private JLabel emailLabel;
    private JLabel passwordLabel;

    private JButton infoButton;
    private JButton accessButton;
    private JButton guestButton;
    private JButton registerButton;


    private final JTextField emailField;
    private final JPasswordField passwordField;


    public LoginPage(MainFrame frame) {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        // Pannello in alto
        topPanel = new JPanel(new BorderLayout());
        infoButton = new JButton("Info");
        infoButton.addActionListener(e ->  frame.setView(new HelpPage(frame)));
        topPanel.add(infoButton, BorderLayout.WEST);
        loginLabel = new JLabel("Login!", JLabel.CENTER );
        topPanel.add(loginLabel, BorderLayout.CENTER);

        //Aggiungo un pannello vuoto a destra per centrare la scritta Login!
        topPanel.add(Box.createHorizontalStrut(infoButton.getPreferredSize().width), BorderLayout.EAST);

        loginPanel.add(topPanel, BorderLayout.NORTH);

        //Pannello centrale
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        //Email
        emailLabel = new JLabel("Email");
        emailField = new JTextField(20);
        emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        emailField.setMaximumSize(emailField.getPreferredSize());
        emailField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        //Password
        passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField(20);


        passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);


        //Bottone Accedi!
        accessButton = new JButton("Accedi!");
        accessButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        // Aggiungo tutto al pannello centrale con gli spazi necessari
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(emailPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(accessButton);
        centerPanel.add(Box.createVerticalGlue());

        loginPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel
        bottomPanel = new JPanel(new BorderLayout());

        guestButton = new JButton("Entra come Ospite");
        registerButton = new JButton("Registrati!");
        registerButton.addActionListener(e -> frame.setView(new RegistrationPage(frame)));

        bottomPanel.add(guestButton, BorderLayout.WEST);
        bottomPanel.add(registerButton, BorderLayout.EAST);

        loginPanel.add(bottomPanel, BorderLayout.SOUTH);

    }



    @Override
    public JPanel getPanel() {
        return loginPanel;
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }


}
