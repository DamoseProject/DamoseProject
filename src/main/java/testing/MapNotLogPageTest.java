package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import gui.MapNotLogPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MapNotLogPageTest {

    private MapNotLogPage mapNotLogPage;

    @BeforeEach
    void setUp() {
        // Nota: Assicurati sempre che il DB sia gestito (mockato o attivo)
        // poiché ereditato da BaseMapPage
        mapNotLogPage = new MapNotLogPage(null);
    }

    @Test
    @DisplayName("MapNotLogPage deve restituire la configurazione per utente Guest")
    void testGetButtonConfigForGuestUser() {
        ButtonMapPageConfig config = mapNotLogPage.getButtonConfig();

        assertNotNull(config, "La configurazione per Guest non deve essere null");

        // Verifichiamo che le restrizioni siano attive
        assertAll("Verifica restrizioni utente non loggato",
                () -> assertFalse(config.isFavoritesEnabled(),
                        "In MapNotLogPage i preferiti devono essere disabilitati"),
                () -> assertFalse(config.isViewFavoritesEnabled(),
                        "In MapNotLogPage la visualizzazione preferiti deve essere disabilitata"),
                () -> assertTrue(config.isShowRegLoginButton(),
                        "In MapNotLogPage il tasto Login deve essere mostrato"),
                () -> assertNotNull(config.getFavoritesErrorMessage(),
                        "Deve esserci un messaggio di errore per i preferiti")
        );
    }

    @Test
    @DisplayName("Verifica che i messaggi di errore siano quelli per Guest")
    void testGuestErrorMessages() {
        ButtonMapPageConfig config = mapNotLogPage.getButtonConfig();

        assertEquals(Constants.LOGIN_REQUIRED_FAVORITES, config.getFavoritesErrorMessage(),
                "Il messaggio di errore per i preferiti deve essere quello di login richiesto");
    }
}