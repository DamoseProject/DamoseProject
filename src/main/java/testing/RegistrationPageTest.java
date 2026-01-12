package testing;

import gui.MainFrame;
import gui.RegistrationPage;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link RegistrationPage}.
 * Verifica la corretta inizializzazione e la gerarchia strutturale della pagina
 * dedicata alla registrazione dei nuovi utenti. Assicura che l'interfaccia
 * sia presentata in uno stato coerente (campi vuoti) e che la disposizione
 * dei componenti rispetti i criteri di layout definiti nel sistema.
 */
class RegistrationPageTest {

    private RegistrationPage registrationPage;
    private MainFrame mockFrame;

    /**
     * Prepara l'ambiente di test prima di ogni singola esecuzione.
     * Crea un'istanza del frame principale e della pagina di registrazione,
     * garantendo l'isolamento tra i test.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        registrationPage = new RegistrationPage(mockFrame);
    }

    /**
     * Valida lo stato iniziale di tutti i campi di input testuale.
     * Verifica che username, email, password e conferma password siano
     * rigorosamente vuoti all'apertura della pagina, garantendo che non
     * vi siano dati residui che potrebbero compromettere la privacy dell'utente.
     */
    @Test
    @DisplayName("I campi di registrazione devono essere inizialmente vuoti")
    void testInitialFieldsEmpty() {
        assertAll("Campi input registrazione",
                () -> assertEquals("", registrationPage.getUsernameRegistration()),
                () -> assertEquals("", registrationPage.getEmailRegistration()),
                () -> assertEquals("", registrationPage.getPasswordRegistration()),
                () -> assertEquals("", registrationPage.getConfirmPasswordRegistration())
        );
    }

    /**
     * Verifica l'integrità strutturale del pannello di registrazione.
     * Ispeziona il layout manager (BorderLayout) e conferma la presenza dei
     * componenti critici nella gerarchia: il pannello superiore (NORTH)
     * per la navigazione e il pannello centrale (CENTER) per il form di inserimento dati.
     */
    @Test
    @DisplayName("Verifica corretta gerarchia dei pannelli")
    void testPanelStructure() {
        JPanel main = registrationPage.getPanel();
        // NORTH: topPanel (con back button), CENTER: centerPanel (con i campi)
        assertTrue(main.getLayout() instanceof java.awt.BorderLayout);
        assertNotNull(main.getComponent(0));
        assertNotNull(main.getComponent(1));
    }
}