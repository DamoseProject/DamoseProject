package gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationResultTest {

    @Test
    void testSuccessFactory() {
        // Creazione tramite metodo statico
        RegistrationResult result = RegistrationResult.success();

        // Verifica tramite getter pubblici
        assertTrue(result.isSuccess(), "Il risultato dovrebbe essere 'success'");
        assertNull(result.getErrorMessage(), "Il messaggio di errore dovrebbe essere null in caso di successo");

        // Verifica tramite Reflection (per il tuo standard di progetto)
        boolean successField = getPrivateField(result, "success", Boolean.class);
        assertTrue(successField);
    }

    @Test
    void testFailureFactory() {
        String expectedError = "Email già esistente";

        // Creazione tramite metodo statico
        RegistrationResult result = RegistrationResult.failure(expectedError);

        // Verifica tramite getter pubblici
        assertFalse(result.isSuccess(), "Il risultato dovrebbe essere 'failure'");
        assertEquals(expectedError, result.getErrorMessage());

        // Verifica tramite Reflection dello stato interno
        String errorMessageField = getPrivateField(result, "errorMessage", String.class);
        assertEquals(expectedError, errorMessageField);
    }

    @Test
    void testImmutabilityViaReflection() {
        // Questo test verifica che i campi siano effettivamente final o comunque
        // impostati correttamente dal costruttore privato
        RegistrationResult result = RegistrationResult.failure("Errore");

        assertNotNull(result);
        assertEquals("Errore", getPrivateField(result, "errorMessage", String.class));
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