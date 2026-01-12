package gui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static gui.UIComponentFactory.*;

/**
 * Questa classe rappresenta la pagina di Login dell'applicazione.
 * Permette agli utenti di inserire le proprie credenziali per accedere,
 * di andare alla pagina di registrazione o di entrare come semplici ospiti.
 */
public class LoginPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JLabel errorAccessLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    /**
     * Costruttore della pagina di login.
     * Prepara i pannelli superiore e centrale e imposta il focus per la tastiera.
     * * @param frame Il frame principale dove viene visualizzata la pagina.
     */
    public LoginPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
    }

    /**
     * Crea la barra superiore della pagina.
     * Contiene il link alla sezione Q&A e il titolo "Login!".
     */
    private void createTopPanel() {
        topPanel = new JPanel(new GridLayout(1, 3));

        JPanel leftPanel = createHorizontalPanel(FlowLayout.LEFT);

        JLabel infoLabel = createClickableLabel("Q&A", Color.BLACK,
                () -> frame.setView(PageFactory.createPage(PageType.HELP, frame))
        );

        leftPanel.add(infoLabel);

        JLabel loginLabel = createLabel("Login!", JLabel.CENTER);
        JPanel rightPanel = new JPanel();

        topPanel.add(leftPanel);
        topPanel.add(loginLabel);
        topPanel.add(rightPanel);
    }

    /**
     * Crea il cuore della pagina di login con i campi Username e Password.
     * Gestisce anche l'allineamento dei componenti e il tasto di accesso.
     */
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

    /**
     * Crea la parte bassa della pagina con i link per chi non ha ancora un account.
     * * @return Un pannello con le scritte cliccabili per registrazione o modalità ospite.
     */
    private JPanel createRegisterPanel() {
        JPanel registerPanel = createHorizontalPanel(FlowLayout.CENTER);
        ((FlowLayout)registerPanel.getLayout()).setHgap(0);
        ((FlowLayout)registerPanel.getLayout()).setVgap(0);

        JLabel registerLabel = createLabel("Non hai un account? Registrati ", JLabel.CENTER);

        JLabel registerButtonLabel = createClickableLabel("qui ", Color.BLUE,
                () -> frame.setView(PageFactory.createPage(PageType.REGISTRATION, frame))
        );

        JLabel registerLabel2 = createLabel("o ", JLabel.CENTER);

        JLabel guestButtonLabel = createClickableLabel("entra come Ospite!", Color.BLUE,
                () -> frame.setView(PageFactory.createPage(PageType.MAP_GUEST, frame))
        );

        registerPanel.add(registerLabel);
        registerPanel.add(registerButtonLabel);
        registerPanel.add(registerLabel2);
        registerPanel.add(guestButtonLabel);

        return registerPanel;
    }

    /**
     * Gestisce la logica del login quando viene premuto il tasto "Accedi!".
     * Verifica le credenziali tramite UserAuth e salva la sessione.
     */
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

    /**
     * @return Lo username inserito nel campo di testo.
     */
    public String getUsernameLogin() {
        return usernameField.getText().trim();
    }

    /**
     * @return La password inserita (convertita da array di char a Stringa).
     */
    public String getPasswordLogin() {
        return new String(passwordField.getPassword());
    }
}