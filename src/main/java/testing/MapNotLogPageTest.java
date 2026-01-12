package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import gui.MainFrame;
import gui.MapNotLogPage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MapNotLogPageTest {

    private MapNotLogPage mapNotLogPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        mapNotLogPage = new MapNotLogPage(mockFrame);
    }

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