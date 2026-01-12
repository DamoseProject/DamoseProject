package testing;

import gui.Constants;
import gui.RegistrationController;
import gui.RegistrationResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link RegistrationController}.
 * Valida il coordinamento della logica di registrazione, assicurando che il controller
 * interpreti correttamente gli input dell'utente e generi i corrispondenti
 * oggetti {@link RegistrationResult} in base all'esito delle validazioni.
 * * <p>Il test si concentra sulla verifica dei casi di errore comuni, come la
 * violazione dei limiti di lunghezza o la mancata corrispondenza delle password.</p>
 */
class RegistrationControllerTest {

    private RegistrationController controller;

    /**
     * Inizializza una nuova istanza del controller prima di ogni metodo di test
     * per garantire l'isolamento dei test (stateless testing).
     */
    @BeforeEach
    void setUp() {
        controller = new RegistrationController();
    }

    /**
     * Verifica che il controller applichi correttamente il vincolo sulla lunghezza
     * dello username definito in {@link Constants#MAX_USERNAME_LENGTH}.
     * Assicura che, in caso di violazione, il risultato sia di fallimento e contenga
     * il messaggio d'errore specifico per l'utente.
     */
    @Test
    @DisplayName("Il controller deve bloccare username troppo lunghi")
    void testUsernameLengthValidation() {
        RegistrationResult result = controller.handleRegistration(
                "unUsernameTroppoLungoOltreDodici",
                "test@gmail.com",
                "Pass123!",
                "Pass123!"
        );

        assertFalse(result.isSuccess());
        assertEquals(Constants.USERNAME_TOO_LONG, result.getErrorMessage());
    }

    /**
     * Valida il controllo di integrità sulla conferma della password.
     * Assicura che il controller rilevi la discrepanza tra il campo 'password' e
     * 'conferma password', bloccando la procedura e restituendo il messaggio
     * {@link Constants#PASSWORD_MISMATCH}.
     */
    @Test
    @DisplayName("Il controller deve bloccare password che non coincidono")
    void testPasswordMatchValidation() {
        RegistrationResult result = controller.handleRegistration(
                "user", "test@gmail.com", "Pass123!", "Diversa123!"
        );

        assertFalse(result.isSuccess());
        assertEquals(Constants.PASSWORD_MISMATCH, result.getErrorMessage());
    }
}