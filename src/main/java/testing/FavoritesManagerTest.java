package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import gui.FavoritesManager;
import gui.UserSession;
import model.*;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class FavoritesManagerTest {

    private FavoritesManager favoritesManager;
    private JLabel errorLabel;
    private UserSession session;

    @BeforeEach
    void setUp() {
        errorLabel = new JLabel();
        session = UserSession.getInstance();
        session.logout(); // Partiamo da uno stato pulito

        // Mocking della configurazione per utente ospite
        ButtonMapPageConfig guestConfig = ButtonMapPageConfig.forGuestUser();
        favoritesManager = new FavoritesManager(null, errorLabel, guestConfig);
    }

    @Test
    @DisplayName("Un utente ospite non deve poter aggiungere preferiti")
    void testGuestCannotAddFavorite() {
        Stop testStop = new Stop(
                "1",
                "123",
                "Fermata Test",
                0.0f,
                0.0f
        );
        JButton favButton = favoritesManager.createFavButtonForStop(testStop);

        // Simuliamo il click
        favButton.doClick();

        assertTrue(errorLabel.isVisible());
        assertEquals(Constants.LOGIN_REQUIRED_FAVORITES, errorLabel.getText());
        assertEquals(java.awt.Color.RED, errorLabel.getForeground());
    }

    @Test
    @DisplayName("L'icona iniziale per un ospite deve essere la stella vuota")
    void testInitialIconForGuest() {
        Stop testStop = new Stop(
                "1",
                "123",
                "Fermata Test",
                0.0f,
                0.0f
        );
        JButton favButton = favoritesManager.createFavButtonForStop(testStop);

        // Il testo contiene l'entità HTML della stella vuota (&#9734;)
        assertTrue(favButton.getText().contains("9734"));
    }
}