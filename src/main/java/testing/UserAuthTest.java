package gui;

import model.Database;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthTest {

    private Database mockDb;
    private UserAuth userAuth;

    @BeforeEach
    void setUp() {
        // Creiamo il mock del Database per isolare la logica di UserAuth
        mockDb = mock(Database.class);
        userAuth = new UserAuth(mockDb);
    }

    @Test
    void testIsUsernameTaken() throws SQLException {
        // Configuriamo il mock per rispondere true quando cerchiamo "mario"
        when(mockDb.isUserRegistered("mario")).thenReturn(true);
        when(mockDb.isUserRegistered("luigi")).thenReturn(false);

        assertTrue(userAuth.isUsernameTaken("mario"));
        assertFalse(userAuth.isUsernameTaken("luigi"));

        // Verifichiamo che il metodo del DB sia stato effettivamente chiamato
        verify(mockDb).isUserRegistered("mario");
    }

    @Test
    void testLoginSuccess() throws SQLException {
        // Setup: creiamo un utente finto che il DB dovrebbe restituire
        User fakeUser = new User();
        fakeUser.setId(1);
        fakeUser.setUsername("mario");
        fakeUser.setPassword("pass123");

        when(mockDb.getUserByUsername("mario")).thenReturn(fakeUser);

        // Esecuzione
        User result = userAuth.login("mario", "pass123");

        // Verifica
        assertNotNull(result);
        assertEquals("mario", result.getUsername());
    }

    @Test
    void testLoginWrongPassword() throws SQLException {
        User fakeUser = new User();
        fakeUser.setPassword("pass123");

        when(mockDb.getUserByUsername("mario")).thenReturn(fakeUser);

        // Password sbagliata
        User result = userAuth.login("mario", "wrong_password");

        assertNull(result, "Il login dovrebbe fallire con password errata");
    }

    @Test
    void testLoginUserNotFound() throws SQLException {
        // Il DB restituisce null se l'utente non esiste
        when(mockDb.getUserByUsername("unknown")).thenReturn(null);

        User result = userAuth.login("unknown", "any_password");

        assertNull(result);
    }

    @Test
    void testReflectionOnDatabaseField() {
        // Verifica dello stato interno tramite il tuo standard Reflection
        Database dbField = getPrivateField(userAuth, "db", Database.class);
        assertEquals(mockDb, dbField, "Il database impostato non corrisponde al mock");
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