package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import gui.MainFrame;
import gui.MapNotLogPage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link MapNotLogPage}.
 * Verifica che la visualizzazione della mappa per utenti non autenticati (Ospiti)
 * applichi correttamente le restrizioni funzionali.
 */
class MapNotLogPageTest {

    private MapNotLogPage mapNotLogPage;
    private MainFrame mockFrame;

    /**
     * Inizializza l'ambiente di test prima di ogni esecuzione.
     * Crea un'istanza del frame principale e della pagina mappa per utenti non loggati
     * per ispezionare i vincoli di configurazione.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        mapNotLogPage = new MapNotLogPage(mockFrame);
    }

    /**
     * Valida l'oggetto di configurazione dei permessi per l'utente ospite.
     * Assicura che:
     * <ul>
     * <li>L'aggiunta ai preferiti sia inibita.</li>
     * <li>Il pulsante di accesso sia visibile nell'interfaccia.</li>
     * <li>Il messaggio di errore visualizzato sia quello definito nelle costanti globali.</li>
     * </ul>
     */
    @Test
    @DisplayName("La pagina deve restituire la configurazione per utente ospite")
    void testConfigIsForGuestUser() {
        ButtonMapPageConfig config = mapNotLogPage.getButtonConfig();

        assertAll("Verifica restrizioni utente ospite",
                () -> assertFalse(config.isFavoritesEnabled(), "I preferiti dovrebbero essere disabilitati"),
                () -> assertTrue(config.isShowRegLoginButton(), "Il tasto login dovrebbe essere visibile"),
                () -> assertEquals(Constants.LOGIN_REQUIRED_FAVORITES, config.getFavoritesErrorMessage())
        );
    }
}