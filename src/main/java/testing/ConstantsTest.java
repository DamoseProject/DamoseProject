package testing;

import gui.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per la classe {@link Constants}.
 * Questo test verifica l'integrità dei parametri di configurazione globali e
 * la validità dei messaggi testuali utilizzati in tutta l'applicazione.
 * Assicura che i limiti di validazione per la sicurezza (password, username)
 * e le policy dei caratteri speciali rimangano invariati durante il refactoring.
 */
class ConstantsTest {

    /**
     * Verifica che i limiti numerici e le stringhe di validazione critica
     * corrispondano ai requisiti tecnici e di sicurezza del sistema.
     * Controlla la lunghezza massima dello username, la lunghezza del codice
     * di verifica e l'elenco dei caratteri speciali ammessi.
     */
    @Test
    @DisplayName("Verifica integrità dei limiti di validazione")
    void testValidationLimits() {
        assertAll("Limiti critici",
                () -> assertEquals(12, Constants.MAX_USERNAME_LENGTH, "Il limite username deve essere 12"),
                () -> assertEquals(6, Constants.VERIFICATION_CODE_LENGTH, "Il codice deve essere di 6 cifre"),
                () -> assertEquals("!$&@#", Constants.SPECIAL_CHARS, "I caratteri speciali devono corrispondere alla policy")
        );
    }

    /**
     * Assicura che i messaggi di errore e le stringhe informative non siano vuoti o composti
     * solo da spazi. Questo test previene regressioni UI dove l'utente potrebbe visualizzare
     * popup di errore privi di spiegazione testuale.
     */
    @Test
    @DisplayName("I messaggi di errore non devono essere vuoti")
    void testMessagesNotEmpty() {

        assertFalse(Constants.USERNAME_REQUIRED.isBlank());
        assertFalse(Constants.CONNECTION_ERROR_DATABASE.isBlank());
        assertFalse(Constants.PASSWORD_WEAK.isBlank());
    }
}