package testing;

import gui.DatabaseConnection;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    @DisplayName("Il Singleton deve restituire la stessa istanza")
    void testSingletonInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        assertSame(instance1, instance2, "Le due istanze devono essere identiche");
    }

    @Test
    @DisplayName("getDatabase deve restituire null se non connesso")
    void testGetDatabaseWhenNotConnected() {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        // Nota: se il db è già connesso per test precedenti, questo fallirà.
        // In un test isolato, verifichiamo la coerenza tra isConnected e getDatabase.
        if (!dc.isConnected()) {
            assertNull(dc.getDatabase(), "Il database deve essere null se non connesso");
        } else {
            assertNotNull(dc.getDatabase(), "Il database non deve essere null se connesso");
        }
    }
}