package testing;

import gui.DatabaseConnection;
import gui.UserAuth;
import model.Database;
import model.User;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link UserAuth}.
 * Verifica la logica di autenticazione degli utenti, assicurando che il sistema
 * protegga correttamente l'accesso ai dati sensibili. Il test valida il comportamento
 * del modulo in scenari critici, come l'inserimento di password errate o il
 * tentativo di accesso da parte di utenti non censiti nel database.
 */
class UserAuthTest {

    private UserAuth auth;
    private Database mockDb;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Recupera l'istanza del database tramite il Singleton {@link DatabaseConnection}
     * e inizializza il modulo {@link UserAuth} per l'ispezione dei metodi di login.
     */
    @BeforeEach
    void setUp() {

        mockDb = DatabaseConnection.getInstance().getDatabase();
        auth = new UserAuth(mockDb);
    }

    /**
     * Verifica che il sistema neghi l'accesso in caso di password non corrispondente.
     * Questo test assicura che il confronto degli hash (o delle stringhe) delle password
     * sia rigoroso e restituisca un oggetto {@code null} invece di un profilo utente valido.
     * * @throws SQLException In caso di errori di comunicazione con il database SQLite.
     */
    @Test
    @DisplayName("Login fallito con password errata")
    void testLoginFailedWrongPassword() throws SQLException {

        User user = auth.login("test", "password_sbagliata");
        assertNull(user, "Il login dovrebbe fallire con una password errata");
    }

    /**
     * Verifica la robustezza del sistema contro tentativi di accesso di utenti inesistenti.
     * Valida che il modulo gestisca correttamente le query che non restituiscono record,
     * impedendo l'accesso e garantendo l'integrità del sistema.
     * * @throws SQLException In caso di errori di comunicazione con il database SQLite.
     */
    @Test
    @DisplayName("Login fallito con utente inesistente")
    void testLoginUserNotFound() throws SQLException {
        User user = auth.login("utente_che_non_esiste", "any_password");
        assertNull(user, "Il login dovrebbe fallire se lo username non è registrato");
    }
}