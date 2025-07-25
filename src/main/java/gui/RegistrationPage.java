package gui;

import javax.swing.*;
import java.awt.*;

public class RegistrationPage implements ViewPanel {
    private JPanel registrationPanel;

    public RegistrationPage(MainFrame frame) {
        registrationPanel = new JPanel();
        registrationPanel.setLayout(new BorderLayout());


        //Top Panel
        JLabel registrationLabel = new JLabel("Registrati!");
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(registrationLabel);




        //Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        //Name
        JLabel name = new JLabel("Nome :");
        JTextField nameField = new JTextField(20);
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        name.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        nameField.setMaximumSize(nameField.getPreferredSize());
        nameField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        namePanel.add(name);
        namePanel.add(nameField);

        //Surname
        JLabel surname = new JLabel("Cognome :");
        JTextField surnameField = new JTextField(20);
        JPanel surnamePanel = new JPanel();
        surnamePanel.setLayout(new BoxLayout(surnamePanel, BoxLayout.Y_AXIS));
        surname.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        surnameField.setMaximumSize(surnameField.getPreferredSize());
        surnameField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        surnamePanel.add(surname);
        surnamePanel.add(surnameField);

        //Email
        JLabel email = new JLabel("Email :");
        JTextField emailField = new JTextField(20);
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        email.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        emailField.setMaximumSize(emailField.getPreferredSize());
        emailField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        emailPanel.add(email);
        emailPanel.add(emailField);

        //Password
        JLabel password = new JLabel("Password :");
        JPasswordField passwordField = new JPasswordField(20);
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        password.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        passwordPanel.add(password);
        passwordPanel.add(passwordField);

        //Confirm Password
        JLabel confirmPassword = new JLabel("Conferma Password :");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        JPanel confirmPasswordPanel = new JPanel();
        confirmPasswordPanel.setLayout(new BoxLayout(confirmPasswordPanel, BoxLayout.Y_AXIS));
        confirmPassword.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        confirmPasswordField.setMaximumSize(confirmPasswordField.getPreferredSize());
        confirmPasswordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        confirmPasswordPanel.add(confirmPassword);
        confirmPasswordPanel.add(confirmPasswordField);

        //Register Button
        JButton registerButton = new JButton("Registrati!");
        registerButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> frame.setView(new LoginPage(frame)));

        //Add all to the centerPanel
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(namePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(surnamePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(emailPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(confirmPasswordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(registerButton);
        centerPanel.add(Box.createVerticalGlue());


        registrationPanel.add(topPanel, BorderLayout.NORTH);
        registrationPanel.add(centerPanel, BorderLayout.CENTER);





    }

    @Override
    public JPanel getPanel() {
        return registrationPanel;
    }
}
