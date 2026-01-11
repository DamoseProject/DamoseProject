package testing;

import gui.ButtonMapPageConfig;
import gui.MapLogPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MapLogPageTest {

    private MapLogPage mapLogPage;

    @BeforeEach
    void setUp() {
        // Nota: se BaseMapPage prova a connettersi al DB nel costruttore,
        // assicurati che il DB sia attivo o mockato come discusso prima.
        mapLogPage = new MapLogPage(null);
    }

    @Test
    @DisplayName("MapLogPage deve restituire la configurazione per utente loggato")
    void testGetButtonConfigForLoggedUser() {
        ButtonMapPageConfig config = mapLogPage.getButtonConfig();

        assertNotNull(config, "La configurazione non deve essere null");

        // Verifichiamo che i permessi siano quelli di un utente loggato
        assertAll("Verifica permessi utente loggato",
                () -> assertTrue(config.isFavoritesEnabled(),
                        "In MapLogPage i preferiti dovrebbero essere abilitati"),
                () -> assertTrue(config.isViewFavoritesEnabled(),
                        "In MapLogPage la visualizzazione preferiti dovrebbe essere abilitata"),
                () -> assertFalse(config.isShowRegLoginButton(),
                        "In MapLogPage il tasto Login non dovrebbe essere mostrato")
        );
    }
}