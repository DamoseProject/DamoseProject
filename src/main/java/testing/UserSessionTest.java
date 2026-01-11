package gui;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private UserSession session;

    @BeforeEach
    void setUp() {
        session = UserSession.getInstance();
        session.logout(); // Reset dello stato prima di ogni test
    }

    @Test
    @DisplayName("Il Singleton deve restituire sempre la stessa istanza")
    void testSingletonIdentity() {
        UserSession anotherInstance = UserSession.getInstance();
        assertSame(session, anotherInstance, "Le istanze devono coincidere");
    }

    @Test
    @DisplayName("Il login deve impostare correttamente i dati utente")
    void testLoginState() {
        session.login(101, "MarioRossi");

        assertAll("Verifica stato login",
                () -> assertTrue(session.isLogged()),
                () -> assertEquals(101, session.getUserId()),
                () -> assertEquals("MarioRossi", session.getUsername())
        );
    }

    @Test
    @DisplayName("Il logout deve annullare i dati e impostare logged a false")
    void testLogoutState() {
        session.login(101, "MarioRossi");
        session.logout();

        assertAll("Verifica stato logout",
                () -> assertFalse(session.isLogged()),
                () -> assertNull(session.getUserId()),
                () -> assertNull(session.getUsername())
        );
    }
}