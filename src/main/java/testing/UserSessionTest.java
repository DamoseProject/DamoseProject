package testing;

import gui.UserSession;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link UserSession}.
 * Verifica la logica di gestione della sessione utente globale.
 * Assicura che l'applicazione mantenga un unico stato di autenticazione coerente
 * in tutta la sua esecuzione, validando il ciclo di vita della sessione:
 * dall'accesso (login) alla disconnessione (logout).
 */
class UserSessionTest {

    private UserSession session;

    /**
     * Prepara l'ambiente di test prima di ogni esecuzione.
     * Recupera l'istanza Singleton di {@link UserSession} e forza un logout
     * per garantire che ogni test parta da uno stato "tabula rasa", evitando
     * interferenze tra test consecutivi.
     */
    @BeforeEach
    void setUp() {
        session = UserSession.getInstance();
        session.logout(); // Reset dello stato prima di ogni test
    }

    /**
     * Verifica l'integrità del pattern Singleton.
     * Assicura che chiamate multiple a getInstance() restituiscano il medesimo
     * riferimento in memoria, condizione necessaria per la coerenza dei dati
     * dell'utente tra le diverse pagine dell'applicazione.
     */
    @Test
    @DisplayName("Il Singleton deve restituire sempre la stessa istanza")
    void testSingletonIdentity() {
        UserSession anotherInstance = UserSession.getInstance();
        assertSame(session, anotherInstance, "Le istanze devono coincidere");
    }

    /**
     * Valida la corretta transizione allo stato di utente autenticato.
     * Verifica che, a seguito del login, l'ID utente e lo username siano
     * memorizzati correttamente e che il flag di autenticazione sia attivo.
     */
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

    /**
     * Verifica la pulizia completa dei dati sensibili al momento del logout.
     * Assicura che dopo la disconnessione non rimangano tracce dell'utente
     * (ID e username devono tornare null) e che il flag di login sia disattivato.
     */
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