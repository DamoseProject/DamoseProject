package gui;

import model.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;



public class LoginPage implements GeneralPanel {
    private final JPanel loginPanel;
    private final JPanel topPanel;
    private final JPanel centerPanel;
    private final JPanel usernamePanel;
    private final JPanel passwordPanel;
    private final JPanel bottomPanel;

    private JLabel loginLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel errorAccessLabel;

    private JButton infoButton;
    private JButton accessButton;
    private JButton guestButton;
    private JButton registerButton;

    private Database userControl;
    private final JTextField usernameField;
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
        usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(usernameField.getPreferredSize());
        usernameField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        //Password
        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);


        passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordField.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);


        //Username dal database
        userControl = new Database();
        userControl.connect();

        //Error asccess label
        errorAccessLabel = new JLabel("");
        errorAccessLabel.setForeground(Color.RED);
        errorAccessLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        errorAccessLabel.setVisible(false);

        //Bottone Accedi!
        accessButton = new JButton("Accedi!");
        accessButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        accessButton.addActionListener(e -> {
            try {
                if(!userControl.isUserRegistered(getUsernameLogin())) {
                    errorAccessLabel.setText("Username o password errata!");
                    errorAccessLabel.setVisible(true);
                } else{
                    errorAccessLabel.setVisible(false);
                    frame.setView(new RegistrationPage(frame));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Aggiungo tutto al pannello centrale con gli spazi necessari
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(usernamePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(accessButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(errorAccessLabel);
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

    public String getUsernameLogin() {
        return usernameField.getText().trim();
    }

    public char[] getPasswordLogin() {
        return passwordField.getPassword();
    }


}
