package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link ButtonMapPageConfig}.
 * Valida la logica di controllo degli accessi (RBAC - Role Based Access Control) dell'applicazione.
 * Assicura che la configurazione dei componenti grafici della mappa vari correttamente
 * tra utenti autenticati e ospiti, garantendo che le restrizioni funzionali e i
 * relativi messaggi di errore siano coerenti con lo stato della sessione.
 */
class ButtonMapPageConfigTest {

    /**
     * Verifica la configurazione prevista per un utente autenticato.
     * In questo scenario, tutte le funzionalità legate ai preferiti devono essere abilitate,
     * il pulsante di login deve essere nascosto (sostituito dal profilo) e non devono
     * esserci messaggi di errore bloccanti.
     */
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

    /**
     * Verifica la configurazione prevista per un utente ospite (Guest).
     * In questo scenario, le funzionalità dei preferiti devono essere disabilitate,
     * deve apparire il pulsante di invito alla registrazione e i messaggi di errore
     * devono informare correttamente l'utente della necessità di effettuare il login.
     */
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