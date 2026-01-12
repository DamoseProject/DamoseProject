package testing;

import gui.RegistrationAuth;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link RegistrationAuth}.
 * Verifica la correttezza delle logiche di validazione applicate durante la fase di registrazione.
 * Il test assicura che il sistema applichi correttamente i filtri di sicurezza per le password,
 * i vincoli sulla lunghezza degli identificativi e le restrizioni sui domini email accettati,
 * garantendo l'integrità dei dati nel database utenti.
 */
class RegistrationAuthTest {

    /**
     * Valida i requisiti di complessità della password.
     * Verifica che la password rispetti la policy di sicurezza che richiede:
     * <ul>
     * <li>Almeno una lettera maiuscola</li>
     * <li>Almeno un numero</li>
     * <li>Almeno un carattere speciale (es. !)</li>
     * </ul>
     * Il test copre sia casi positivi che diverse tipologie di fallimento (mancanza di maiuscole o simboli).
     */
    @Test
    @DisplayName("La password deve contenere maiuscola, numero e carattere speciale")
    void testPasswordStrength() {

        RegistrationAuth authValid = new RegistrationAuth("user", "mail@gmail.com", "Password123!", "Password123!");
        assertTrue(authValid.validatePasswordStrength(), "Dovrebbe accettare 'Password123!'");


        RegistrationAuth authNoUpper = new RegistrationAuth("user", "mail@gmail.com", "password123!", "password123!");
        assertFalse(authNoUpper.validatePasswordStrength(), "Dovrebbe rifiutare senza maiuscola");


        RegistrationAuth authNoSpecial = new RegistrationAuth("user", "mail@gmail.com", "Password123", "Password123");
        assertFalse(authNoSpecial.validatePasswordStrength(), "Dovrebbe rifiutare senza carattere speciale");
    }

    /**
     * Verifica il filtro applicato agli indirizzi email.
     * Assicura che l'applicazione accetti solo email provenienti da domini considerati
     * attendibili (es. gmail.com) e scarti domini non supportati o potenzialmente non validi.
     */
    @Test
    @DisplayName("L'email deve avere un dominio supportato")
    void testEmailValidation() {
        RegistrationAuth authOk = new RegistrationAuth("u", "test@gmail.com", "p", "p");
        assertTrue(authOk.validateEmail());

        RegistrationAuth authWrong = new RegistrationAuth("u", "test@libero.it", "p", "p");
        assertFalse(authWrong.validateEmail(), "Dovrebbe rifiutare domini non in lista");
    }

    /**
     * Valida il vincolo di lunghezza massima sullo username.
     * Il test incrocia i dati inseriti con i limiti definiti globalmente per assicurarsi
     * che lo username non superi la dimensione massima consentita dal database.
     */
    @Test
    @DisplayName("Username non deve superare la lunghezza massima")
    void testUsernameLength() {

        RegistrationAuth authLong = new RegistrationAuth("questoUsernameELontissimo", "m@g.com", "p", "p");
        assertFalse(authLong.validateLengthUsername());
    }
}