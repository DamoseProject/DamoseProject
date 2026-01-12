package gui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static gui.UIComponentFactory.*;

/**
 * Questa classe rappresenta la pagina di registrazione dell'applicazione.
 * Fornisce i campi necessari per creare un nuovo account (Username, Email, Password
 * e Conferma Password) e gestisce l'interazione con l'utente durante la creazione del profilo.
 */
public class RegistrationPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JLabel errorLabel;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    /**
     * Costruttore della pagina di registrazione.
     * Inizializza i componenti grafici e li dispone all'interno della finestra.
     * * @param frame Il frame principale (MainFrame) su cui caricare la pagina.
     */
    public RegistrationPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Crea la barra superiore della pagina con il titolo e il pulsante per tornare al Login.
     */
    private void createTopPanel() {
        topPanel = createTopPanelWithBackButton("Registrati!", PageType.LOGIN);
    }

    /**
     * Costruisce il modulo centrale con tutti i campi di input.
     * Organizza verticalmente etichette, campi di testo e il pulsante di conferma.
     */
    private void createCenterPanel() {
        centerPanel = UIComponentFactory.createVerticalPanel();

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

        JButton registerButton = createStyledButton("Registrati!");
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

    /**
     * Gestisce la logica di registrazione quando l'utente preme il tasto o preme invio.
     * Invia i dati al {@link RegistrationController} e, in caso di successo, esegue
     * automaticamente il login portando l'utente alla mappa.
     */
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
                showError(errorLabel, Constants.CONNECTION_ERROR_DATABASE);
                return;
            }

            try {
                User newUser = db.getUserByUsername(userText);

                if (newUser != null) {
                    UserSession.getInstance().login(newUser.getId(), newUser.getUsername());
                    errorLabel.setVisible(false);
                    frame.setView(PageFactory.createPage(PageType.MAP_LOGGED, frame));
                } else {
                    showError(errorLabel, "Registrazione avvenuta, ma errore nel login automatico.");
                }
            } catch (SQLException ex) {
                showError(errorLabel, Constants.CONNECTION_ERROR_DATABASE);
            }
        } else {
            showError(errorLabel, result.getErrorMessage());
        }
    }

    /** @return Lo username inserito nel campo di testo. */
    public String getUsernameRegistration() {
        return usernameField.getText().trim();
    }

    /** @return L'email inserita nel campo di testo. */
    public String getEmailRegistration() {
        return emailField.getText().trim();
    }

    /** @return La password inserita. */
    public String getPasswordRegistration() {
        return new String(passwordField.getPassword());
    }

    /** @return La conferma della password inserita. */
    public String getConfirmPasswordRegistration() {
        return new String(confirmPasswordField.getPassword());
    }
}