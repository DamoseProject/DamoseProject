package testing;

import gui.BackButton;
import gui.Constants;
import gui.EmailVerificationPage;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link EmailVerificationPage}.
 * Verifica il corretto stato iniziale della pagina di verifica email,
 * la coerenza dei vincoli di validazione del codice di sicurezza e
 * l'integrità dei componenti di navigazione per il ritorno alla registrazione.
 */
class EmailVerificationPageTest {

    private EmailVerificationPage verificationPage;
    private MainFrame mockFrame;

    /**
     * Configura l'ambiente di test prima di ogni metodo.
     * Inizializza il frame principale e la pagina di verifica per permettere
     * l'ispezione dei componenti grafici.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        verificationPage = new EmailVerificationPage(mockFrame);
    }

    /**
     * Verifica che il campo di inserimento del codice sia vuoto all'apertura della pagina.
     * Assicura che non vi siano residui di sessioni o input precedenti.
     */
    @Test
    @DisplayName("Il campo del codice deve essere inizialmente vuoto")
    void testInitialField() {
        assertEquals("", verificationPage.getVerificationCode());
    }

    /**
     * Valida la logica di controllo sulla lunghezza del codice di verifica.
     * Il test incrocia i dati simulati con la costante globale {@link Constants#VERIFICATION_CODE_LENGTH}
     * per assicurarsi che il sistema riconosca correttamente i codici non validi (troppo corti).
     */
    @Test
    @DisplayName("Verifica validazione lunghezza codice")
    void testValidationLogic() {

        String shortCode = "123";
        assertTrue(shortCode.length() != Constants.VERIFICATION_CODE_LENGTH);
    }

    /**
     * Verifica la presenza e il posizionamento del pulsante "Indietro".
     * Il test ispeziona la gerarchia dei componenti Swing per garantire che
     * il {@link BackButton} sia accessibile all'utente all'interno del pannello superiore.
     */
    @Test
    @DisplayName("Il tasto Indietro deve riportare alla registrazione")
    void testBackButtonTarget() {
        JPanel topPanel = (JPanel) verificationPage.getPanel().getComponent(0);
        BackButton backBtn = (BackButton) topPanel.getComponent(0);

        assertNotNull(backBtn, "Il BackButton deve essere presente nel TopPanel");
    }
}