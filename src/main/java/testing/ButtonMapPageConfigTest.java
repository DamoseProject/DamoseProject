package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ButtonMapPageConfigTest {

    @Test
    @DisplayName("Configurazione Utente Loggato: tutti i permessi attivi")
    void testLoggedUserConfig() {
        ButtonMapPageConfig config = ButtonMapPageConfig.forLoggedUser();

        assertAll("Permessi Logged User",
                () -> assertTrue(config.isFavoritesEnabled()),
                () -> assertTrue(config.isViewFavoritesEnabled()),
                () -> assertFalse(config.isShowRegLoginButton()),
                () -> assertNull(config.getFavoritesErrorMessage())
        );
    }

    @Test
    @DisplayName("Configurazione Ospite: permessi disattivati e messaggi presenti")
    void testGuestUserConfig() {
        ButtonMapPageConfig config = ButtonMapPageConfig.forGuestUser();

        assertAll("Restrizioni Guest User",
                () -> assertFalse(config.isFavoritesEnabled()),
                () -> assertFalse(config.isViewFavoritesEnabled()),
                () -> assertTrue(config.isShowRegLoginButton()),
                () -> assertEquals(Constants.LOGIN_REQUIRED_FAVORITES, config.getFavoritesErrorMessage()),
                () -> assertEquals(Constants.LOGIN_REQUIRED_LIST, config.getViewFavoritesErrorMessage())
        );
    }
}