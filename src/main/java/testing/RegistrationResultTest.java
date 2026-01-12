package testing;

import gui.RegistrationResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link RegistrationResult}.
 * Verifica l'integrità dell'oggetto di trasporto dati (DTO) utilizzato per
 * incapsulare l'esito della procedura di registrazione.
 * * <p>Assicura che i metodi factory statici creino correttamente lo stato interno
 * dell'oggetto, permettendo alla UI di distinguere chiaramente tra un'operazione
 * andata a buon fine e un errore da segnalare all'utente.</p>
 */
class RegistrationResultTest {

    /**
     * Verifica la creazione di un esito positivo.
     * Assicura che, chiamando il metodo {@code success()}, l'oggetto risultante
     * abbia il flag di successo impostato a {@code true} e che non sia presente
     * alcun messaggio di errore, come previsto dal protocollo di comunicazione interno.
     */
    @Test
    @DisplayName("Il risultato di successo deve avere success=true e nessun errore")
    void testSuccessResult() {
        RegistrationResult result = RegistrationResult.success();
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
    }

    /**
     * Verifica la creazione di un esito negativo.
     * Valida che, chiamando il metodo {@code failure(String)}, l'oggetto
     * contenga il flag di successo a {@code false} e trasporti correttamente
     * la stringa di errore passata, necessaria per informare l'utente sulla
     * causa del fallimento (es. password debole o email non valida).
     */
    @Test
    @DisplayName("Il risultato di fallimento deve avere success=false e il messaggio corretto")
    void testFailureResult() {
        String errorMsg = "Errore di test";
        RegistrationResult result = RegistrationResult.failure(errorMsg);
        assertFalse(result.isSuccess());
        assertEquals(errorMsg, result.getErrorMessage());
    }
}