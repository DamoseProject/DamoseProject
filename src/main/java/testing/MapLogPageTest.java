package testing;

import gui.ButtonMapPageConfig;
import gui.MainFrame;
import gui.MapLogPage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MapLogPageTest {

    private MapLogPage mapLogPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        mapLogPage = new MapLogPage(mockFrame);
    }

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

    @Test
    @DisplayName("Il pannello principale non deve essere nullo")
    void testPanelPresence() {
        assertNotNull(mapLogPage.getPanel());
    }
}