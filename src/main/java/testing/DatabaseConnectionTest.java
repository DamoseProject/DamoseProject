package testing;

import gui.DatabaseConnection;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link DatabaseConnection}.
 * Verifica il corretto funzionamento del pattern Singleton e la gestione dello stato
 * della connessione al database. Questo test assicura che l'applicazione mantenga
 * un unico punto di accesso al database, prevenendo perdite di memoria e
 * corruzione dei dati dovute a connessioni multiple concorrenti.
 */
class DatabaseConnectionTest {

    /**
     * Verifica che il metodo getInstance restituisca sempre lo stesso oggetto in memoria.
     * Questo test valida l'implementazione del pattern Singleton, fondamentale per
     * la coerenza globale dello stato della connessione.
     */
    @Test
    @DisplayName("Il Singleton deve restituire la stessa istanza")
    void testSingletonInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        assertSame(instance1, instance2, "Le due istanze devono essere identiche");
    }

    /**
     * Verifica la coerenza tra lo stato della connessione e l'accessibilità dell'oggetto Database.
     * Il test valida che l'accesso al database sia consentito solo quando la connessione
     * è effettivamente stabilita, prevenendo NullPointerException in fase di esecuzione.
     * * <p>Nota: il test si adatta dinamicamente allo stato attuale dell'istanza condivisa
     * per evitare falsi negativi dovuti a test precedenti che potrebbero aver aperto la connessione.</p>
     */
    @Test
    @DisplayName("getDatabase deve restituire null se non connesso")
    void testGetDatabaseWhenNotConnected() {
        DatabaseConnection dc = DatabaseConnection.getInstance();
        if (!dc.isConnected()) {
            assertNull(dc.getDatabase(), "Il database deve essere null se non connesso");
        } else {
            assertNotNull(dc.getDatabase(), "Il database non deve essere null se connesso");
        }
    }
}