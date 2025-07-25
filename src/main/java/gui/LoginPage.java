package gui;

import javax.swing.*;
import java.awt.*;



public class LoginPage implements ViewPanel {
    private JPanel loginPanel;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage(MainFrame frame) {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        // Pannello in alto
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton helpButton = new JButton("Help");
        topPanel.add(helpButton, BorderLayout.WEST);
        JLabel loginLabel = new JLabel("Login!", JLabel.CENTER  );
        topPanel.add(loginLabel, BorderLayout.CENTER);

        //Aggiungo un pannello vuoto a destra per centrare la scritta Login!
        topPanel.add(Box.createHorizontalStrut(helpButton.getPreferredSize().width), BorderLayout.EAST);

        loginPanel.add(topPanel, BorderLayout.NORTH);

        //Pannello centrale
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        //Email
        JLabel emailLabel = new JLabel("Email");
        JTextField emailField = new JTextField(20);
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        emailField.setMaximumSize(emailField.getPreferredSize());
        emailField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        //Password
        JLabel passwordLabel = new JLabel("Password");
        JTextField passwordField = new JTextField(20);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        //Bottone Accedi!
        JButton accediButton = new JButton("Accedi!");
        accediButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        // Aggiungo tutto al pannello centrale con gli spazi necessari
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(emailPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(accediButton);
        centerPanel.add(Box.createVerticalGlue());

        loginPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton guestButton = new JButton("Entra come Ospite");
        JButton registerButton = new JButton("Registrati!");
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
