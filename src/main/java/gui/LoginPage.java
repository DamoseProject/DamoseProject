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

        JLabel infoLabel = new JLabel("Q&A");
        infoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                infoLabel.setText("<html><u>Q&A</u></html>");
            }

            public void mouseExited(MouseEvent e) {
                infoLabel.setText("Q&A");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setView(PageFactory.createPage(PageType.HELP, frame));
            }

        });

        leftPanel.add(infoLabel);
        JLabel loginLabel = new JLabel("Login!", SwingConstants.CENTER);

        JPanel rightPanel = new JPanel();

        topPanel.add(leftPanel);
        topPanel.add(loginLabel);
        topPanel.add(rightPanel);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        usernameField = new JTextField(20);
        JPanel usernamePanel = createFieldPanel("Username: ", usernameField);

        passwordField = new JPasswordField(20);
        JPanel passwordPanel = createFieldPanel("Password: ", passwordField);
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);

        ActionListener actionListener = e -> handleLogin();

        usernameField.addActionListener(actionListener);
        passwordField.addActionListener(actionListener);

        errorAccessLabel = createErrorLabel();

        JButton accessButton = new JButton("Accedi!");
        accessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        accessButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        accessButton.addActionListener(e -> handleLogin());

        JLabel registerLabel = new JLabel("Non hai un account? Registrati ", JLabel.CENTER);
        JLabel registerButtonLabel = new JLabel("qui ", JLabel.CENTER);
        registerButtonLabel.setForeground(Color.BLUE);
        registerButtonLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel registerLabel2 = new JLabel("o ", JLabel.CENTER);
        JLabel guestButtonLabel = new JLabel("entra come Ospite!", JLabel.CENTER);
        guestButtonLabel.setForeground(Color.BLUE);
        guestButtonLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerButtonLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setView(PageFactory.createPage(PageType.REGISTRATION, frame));
            }

            public void mouseEntered(MouseEvent e) {
                registerButtonLabel.setText("<html><u>qui </u></html>");
            }

            public void mouseExited(MouseEvent e) {
                registerButtonLabel.setText("qui ");
            }
        });

        guestButtonLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setView(PageFactory.createPage(PageType.MAP_GUEST, frame));
            }

            public void mouseEntered(MouseEvent e) {
                guestButtonLabel.setText("<html><u>entra come Ospite!</u></html>");
            }

            public void mouseExited(MouseEvent e) {
                guestButtonLabel.setText("entra come Ospite!");
            }
        });

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        registerPanel.add(registerLabel);
        registerPanel.add(registerButtonLabel);
        registerPanel.add(registerLabel2);
        registerPanel.add(guestButtonLabel);

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

        centerPanel.setLayout(new GridBagLayout());
        centerPanel.add(contentPanel, new GridBagConstraints());
    }

    private void handleLogin() {
        var db = DatabaseConnection.getInstance().getDatabase();

        if (db == null) {
            errorAccessLabel.setText(Constants.CONNECTION_ERROR_DATABASE);
            errorAccessLabel.setVisible(true);
            return;
        }

        try {
            UserAuth auth = new UserAuth(db);
            User user = auth.login(getUsernameLogin(), getPasswordLogin());

            if (user == null) {
                errorAccessLabel.setText(Constants.USERNAME_OR_PSW_WRONG);
                errorAccessLabel.setVisible(true);
            } else {
                UserSession.getInstance().login(user.getId(), user.getUsername());
                errorAccessLabel.setVisible(false);
                frame.setView(PageFactory.createPage(PageType.MAP_LOGGED, frame));
            }

        } catch (SQLException ex) {
            errorAccessLabel.setText(Constants.CONNECTION_ERROR_DATABASE);
            errorAccessLabel.setVisible(true);
        }
    }

    public String getUsernameLogin() {
        return usernameField.getText().trim();
    }

    public String getPasswordLogin() {
        return new String(passwordField.getPassword());
    }
}