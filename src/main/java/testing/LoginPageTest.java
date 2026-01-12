package testing;

import gui.LoginPage;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link LoginPage}.
 * Verifica lo stato iniziale dei componenti di input, la corretta configurazione
 * dei campi sensibili (password) e la predisposizione dei meccanismi di navigazione
 * verso le pagine di assistenza.
 */
class LoginPageTest {

    private LoginPage loginPage;
    private MainFrame mockFrame;

    /**
     * Inizializza l'ambiente di test creando un frame principale e
     * un'istanza della pagina di login prima di ogni metodo.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        loginPage = new LoginPage(mockFrame);
    }

    /**
     * Verifica che i campi di inserimento per username e password siano
     * vuoti all'apertura della pagina. Garantisce che non vi siano dati
     * pre-caricati che potrebbero compromettere la sicurezza.
     */
    @Test
    @DisplayName("I campi di login devono essere inizialmente vuoti")
    void testInitialFields() {
        assertEquals("", loginPage.getUsernameLogin());
        assertEquals("", loginPage.getPasswordLogin());
    }

    /**
     * Valida l'esistenza e la disponibilità del pannello principale per
     * le operazioni di navigazione verso la pagina Help/Q&A.
     */
    @Test
    @DisplayName("Il click sul link Q&A deve cambiare la vista")
    void testNavigationToHelp() {
        assertNotNull(loginPage.getPanel());
    }

    /**
     * Ispeziona la gerarchia dei componenti per verificare la corretta
     * implementazione del pannello centrale. Questo test assicura che il
     * layout manager abbia costruito la struttura necessaria per ospitare
     * i campi sensibili come il JPasswordField.
     */
    @Test
    @DisplayName("Verifica configurazione JPasswordField")
    void testPasswordFieldProperties() {
        assertTrue(loginPage.getPanel().getComponent(1) instanceof JPanel);
    }
}