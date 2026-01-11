package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ButtonMapPageConfigTest {

    @Test
    @DisplayName("Il costruttore dovrebbe assegnare correttamente tutti i campi")
    void testConstructor() {
        ButtonMapPageConfig config = new ButtonMapPageConfig(
                true, false, true, "Errore 1", "Errore 2"
        );

        assertAll("Verifica di tutti i campi del costruttore",
                () -> assertTrue(config.isFavoritesEnabled()),
                () -> assertFalse(config.isViewFavoritesEnabled()),
                () -> assertTrue(config.isShowRegLoginButton()),
                () -> assertEquals("Errore 1", config.getFavoritesErrorMessage()),
                () -> assertEquals("Errore 2", config.getViewFavoritesErrorMessage())
        );
    }

    @Test
    @DisplayName("forLoggedUser dovrebbe avere i permessi abilitati e nessun errore")
    void testForLoggedUser() {
        ButtonMapPageConfig config = ButtonMapPageConfig.forLoggedUser();

        assertTrue(config.isFavoritesEnabled(), "Un utente loggato deve poter aggiungere preferiti");
        assertTrue(config.isViewFavoritesEnabled(), "Un utente loggato deve poter vedere i preferiti");
        assertFalse(config.isShowRegLoginButton(), "Un utente loggato non deve vedere il tasto Login");
        assertNull(config.getFavoritesErrorMessage(), "Non dovrebbe esserci un messaggio di errore per i preferiti");
    }

    @Test
    @DisplayName("forGuestUser dovrebbe avere i permessi disabilitati e messaggi di errore corretti")
    void testForGuestUser() {
        ButtonMapPageConfig config = ButtonMapPageConfig.forGuestUser();

        assertFalse(config.isFavoritesEnabled(), "Un ospite non può aggiungere preferiti");
        assertFalse(config.isViewFavoritesEnabled(), "Un ospite non può vedere i preferiti");
        assertTrue(config.isShowRegLoginButton(), "Un ospite deve vedere il tasto Login");

        // Verifichiamo che i messaggi non siano nulli o vuoti
        assertNotNull(config.getFavoritesErrorMessage());
        assertNotNull(config.getViewFavoritesErrorMessage());

        // Se ErrorMessages è una costante, puoi verificare il valore esatto
        assertEquals(Constants.LOGIN_REQUIRED_FAVORITES, config.getFavoritesErrorMessage());
    }
}