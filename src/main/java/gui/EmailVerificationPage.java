package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Questa classe rappresenta la pagina di verifica dell'email.
 * È la schermata dove l'utente deve inserire il codice a 6 cifre ricevuto via email
 * per confermare che l'indirizzo sia corretto e completare la registrazione.
 */
public class EmailVerificationPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel verificationCodePanel;
    private JTextField verificationCodeField;
    private JLabel errorVerificationCodeLabel;

    /**
     * Costruttore della pagina. Prepara la barra in alto e il modulo al centro.
     * @param frame Il frame principale dove caricare la pagina.
     */
    public EmailVerificationPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Crea la parte superiore della pagina con il titolo e il tasto per tornare indietro.
     */
    private void createTopPanel() {
        topPanel = createTopPanelWithBackButton("Completa la tua registrazione!", PageType.REGISTRATION);
    }

    /**
     * Crea la parte centrale della pagina con il campo per il codice e il tasto di conferma.
     */
    private void createCenterPanel() {
        centerPanel = UIComponentFactory.createVerticalPanel();

        createVerificationCodePanel();
        errorVerificationCodeLabel = UIComponentFactory.createErrorLabel();

        JButton submitButton = UIComponentFactory.createStyledButton("Fine!");
        submitButton.addActionListener(e -> handleSubmit());

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(verificationCodePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(submitButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(errorVerificationCodeLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    /**
     * Crea il riquadro specifico dove scrivere il codice di verifica.
     */
    private void createVerificationCodePanel() {
        verificationCodeField = new JTextField(6);
        verificationCodeField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, verificationCodeField.getPreferredSize().height)
        );

        verificationCodePanel = createFieldPanel(
                "Inserisci il codice di verifica a 6 cifre che è stato inviato alla tua e-mail:",
                verificationCodeField
        );

        verificationCodePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Gestisce cosa succede quando l'utente preme il tasto "Fine!".
     * Controlla se il codice è vuoto o se ha la lunghezza sbagliata prima di procedere.
     */
    private void handleSubmit() {
        String code = getVerificationCode();

        if (code.isEmpty()) {
            showError(errorVerificationCodeLabel, Constants.MISSED_VER_CODE);
        } else if (code.length() != Constants.VERIFICATION_CODE_LENGTH) {
            showError(errorVerificationCodeLabel, Constants.WRONG_VER_CODE);
        } else {
            errorVerificationCodeLabel.setVisible(false);
            frame.setView(new MapLogPage(frame));
        }
    }

    /**
     * Recupera il testo inserito dall'utente nel campo del codice.
     * @return Il codice inserito senza spazi bianchi.
     */
    public String getVerificationCode() {
        return verificationCodeField.getText().trim();
    }
}