package testing;

import gui.RegistrationAuth;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationAuthTest {

    @Test
    void testValidatePresenceUsername() {
        // Test username presente
        RegistrationAuth authValid = new RegistrationAuth("User", "mail@test.it", "Psw123!", "Psw123!");
        assertTrue(authValid.validatePresenceUsername());

        // Test username vuoto
        RegistrationAuth authInvalid = new RegistrationAuth("", "mail@test.it", "Psw123!", "Psw123!");
        assertFalse(authInvalid.validatePresenceUsername());
    }

    @Test
    void testValidatePasswordMatch() {
        // Password uguali
        RegistrationAuth authMatch = new RegistrationAuth("User", "mail", "Pass1!", "Pass1!");
        assertTrue(authMatch.validatePasswordMatch());

        // Password diverse
        RegistrationAuth authNoMatch = new RegistrationAuth("User", "mail", "Pass1!", "Pass2?");
        assertFalse(authNoMatch.validatePasswordMatch());
    }

    @Test
    void testValidatePasswordStrength() {
        // Password forte (Maiuscola, Numero, Speciale)
        RegistrationAuth strong = new RegistrationAuth("User", "mail", "Strong1!", "Strong1!");
        assertTrue(strong.validatePasswordStrength());

        // Password senza maiuscola
        RegistrationAuth noUpper = new RegistrationAuth("User", "mail", "weak1!", "weak1!");
        assertFalse(noUpper.validatePasswordStrength());

        // Password senza numero
        RegistrationAuth noDigit = new RegistrationAuth("User", "mail", "Weakness!", "Weakness!");
        assertFalse(noDigit.validatePasswordStrength());

        // Password senza carattere speciale
        RegistrationAuth noSpecial = new RegistrationAuth("User", "mail", "Weakness1", "Weakness1");
        assertFalse(noSpecial.validatePasswordStrength());
    }

    @Test
    void testReflectionOnPrivateFields() {
        // Esempio di utilizzo del tuo standard Reflection per verificare
        // che il costruttore assegni correttamente i campi privati
        RegistrationAuth auth = new RegistrationAuth("Admin", "admin@bus.it", "Secret123!", "Secret123!");

        String username = getPrivateField(auth, "username", String.class);
        String email = getPrivateField(auth, "email", String.class);

        assertEquals("Admin", username);
        assertEquals("admin@bus.it", email);
    }

    /**
     * Utility reflection per accedere a campi privati (Standard di progetto)
     */
    private static <T> T getPrivateField(Object obj, String fieldName, Class<T> type) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(obj));
        } catch (Exception e) {
            throw new RuntimeException("Impossibile accedere al campo: " + fieldName, e);
        }
    }
}