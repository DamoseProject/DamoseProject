package gui;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationAuthTest {

    @Test
    @DisplayName("La password deve contenere maiuscola, numero e carattere speciale")
    void testPasswordStrength() {
        // Password valida
        RegistrationAuth authValid = new RegistrationAuth("user", "mail@gmail.com", "Password123!", "Password123!");
        assertTrue(authValid.validatePasswordStrength(), "Dovrebbe accettare 'Password123!'");

        // Password senza maiuscola
        RegistrationAuth authNoUpper = new RegistrationAuth("user", "mail@gmail.com", "password123!", "password123!");
        assertFalse(authNoUpper.validatePasswordStrength(), "Dovrebbe rifiutare senza maiuscola");

        // Password senza caratteri speciali
        RegistrationAuth authNoSpecial = new RegistrationAuth("user", "mail@gmail.com", "Password123", "Password123");
        assertFalse(authNoSpecial.validatePasswordStrength(), "Dovrebbe rifiutare senza carattere speciale");
    }

    @Test
    @DisplayName("L'email deve avere un dominio supportato")
    void testEmailValidation() {
        RegistrationAuth authOk = new RegistrationAuth("u", "test@gmail.com", "p", "p");
        assertTrue(authOk.validateEmail());

        RegistrationAuth authWrong = new RegistrationAuth("u", "test@libero.it", "p", "p");
        assertFalse(authWrong.validateEmail(), "Dovrebbe rifiutare domini non in lista");
    }

    @Test
    @DisplayName("Username non deve superare la lunghezza massima")
    void testUsernameLength() {
        // Supponendo Constants.MAX_USERNAME_LENGTH = 12
        RegistrationAuth authLong = new RegistrationAuth("questoUsernameELontissimo", "m@g.com", "p", "p");
        assertFalse(authLong.validateLengthUsername());
    }
}