package gui;

import model.Database;
import model.User;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class UserAuthTest {

    private UserAuth auth;
    private Database mockDb; // In un test reale useresti un mock o un DB di test

    @BeforeEach
    void setUp() {
        // Supponiamo che DatabaseConnection fornisca un'istanza valida per il test
        mockDb = DatabaseConnection.getInstance().getDatabase();
        auth = new UserAuth(mockDb);
    }

    @Test
    @DisplayName("Login fallito con password errata")
    void testLoginFailedWrongPassword() throws SQLException {
        // Questo test presuppone che esista un utente 'test' con password '123' nel DB di test
        User user = auth.login("test", "password_sbagliata");
        assertNull(user, "Il login dovrebbe fallire con una password errata");
    }

    @Test
    @DisplayName("Login fallito con utente inesistente")
    void testLoginUserNotFound() throws SQLException {
        User user = auth.login("utente_che_non_esiste", "any_password");
        assertNull(user, "Il login dovrebbe fallire se lo username non è registrato");
    }
}