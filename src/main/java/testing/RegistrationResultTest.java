package testing;

import gui.RegistrationResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationResultTest {

    @Test
    @DisplayName("Il risultato di successo deve avere success=true e nessun errore")
    void testSuccessResult() {
        RegistrationResult result = RegistrationResult.success();
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
    }

    @Test
    @DisplayName("Il risultato di fallimento deve avere success=false e il messaggio corretto")
    void testFailureResult() {
        String errorMsg = "Errore di test";
        RegistrationResult result = RegistrationResult.failure(errorMsg);
        assertFalse(result.isSuccess());
        assertEquals(errorMsg, result.getErrorMessage());
    }
}