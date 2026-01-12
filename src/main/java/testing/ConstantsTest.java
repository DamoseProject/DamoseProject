package testing;

import gui.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    @DisplayName("Verifica integrità dei limiti di validazione")
    void testValidationLimits() {
        assertAll("Limiti critici",
                () -> assertEquals(12, Constants.MAX_USERNAME_LENGTH, "Il limite username deve essere 12"),
                () -> assertEquals(6, Constants.VERIFICATION_CODE_LENGTH, "Il codice deve essere di 6 cifre"),
                () -> assertEquals("!$&@#", Constants.SPECIAL_CHARS, "I caratteri speciali devono corrispondere alla policy")
        );
    }

    @Test
    @DisplayName("I messaggi di errore non devono essere vuoti")
    void testMessagesNotEmpty() {
        // Test a campione su messaggi critici
        assertFalse(Constants.USERNAME_REQUIRED.isBlank());
        assertFalse(Constants.CONNECTION_ERROR_DATABASE.isBlank());
        assertFalse(Constants.PASSWORD_WEAK.isBlank());
    }
}