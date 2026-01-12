package testing;

import gui.ButtonMapPageConfig;
import gui.MainFrame;
import gui.MapLogPage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link MapLogPage}.
 * Verifica che la pagina della mappa destinata agli utenti autenticati carichi
 * correttamente il set di permessi esteso.
 */
class MapLogPageTest {

    private MapLogPage mapLogPage;
    private MainFrame mockFrame;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Crea un'istanza di {@link MainFrame} e della pagina {@link MapLogPage}
     * per ispezionare lo stato iniziale dei componenti e dei permessi.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        mapLogPage = new MapLogPage(mockFrame);
    }

    /**
     * Valida la configurazione dei permessi restituita dalla pagina.
     * Utilizza un'asserzione di gruppo per verificare che l'utente loggato
     * possa interagire con i preferiti (aggiunta e visualizzazione) e che
     * l'interfaccia non mostri più inviti alla registrazione/login.
     */
    @Test
    @DisplayName("La pagina deve restituire la configurazione per utente loggato")
    void testConfigIsForLoggedUser() {
        ButtonMapPageConfig config = mapLogPage.getButtonConfig();

        assertAll("Verifica permessi utente loggato",
                () -> assertTrue(config.isFavoritesEnabled(), "I preferiti dovrebbero essere abilitati"),
                () -> assertTrue(config.isViewFavoritesEnabled(), "La visualizzazione preferiti dovrebbe essere abilitata"),
                () -> assertFalse(config.isShowRegLoginButton(), "Il tasto login non dovrebbe essere mostrato")
        );
    }

    /**
     * Verifica la corretta inizializzazione dell'interfaccia grafica.
     * Assicura che il pannello principale sia stato istanziato e sia pronto
     * per essere inserito nel {@link MainFrame}.
     */
    @Test
    @DisplayName("Il pannello principale non deve essere nullo")
    void testPanelPresence() {
        assertNotNull(mapLogPage.getPanel());
    }
}