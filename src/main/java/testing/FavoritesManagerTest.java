package testing;

import gui.ButtonMapPageConfig;
import gui.Constants;
import gui.FavoritesManager;
import gui.UserSession;
import model.*;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link FavoritesManager}.
 * Verifica la logica di gestione dei preferiti, concentrandosi in particolare
 * sulle restrizioni di sicurezza per gli utenti non autenticati (Guest).
 * Assicura che il sistema risponda con i corretti messaggi di errore e
 * che l'iconografia del pulsante sia coerente con lo stato della sessione.
 */
class FavoritesManagerTest {

    private FavoritesManager favoritesManager;
    private JLabel errorLabel;
    private UserSession session;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Garantisce che la {@link UserSession} sia resettata (logout) per simulare
     * correttamente un utente ospite e inizializza il manager con una
     * configurazione restrittiva.
     */
    @BeforeEach
    void setUp() {
        errorLabel = new JLabel();
        session = UserSession.getInstance();
        session.logout(); // Partiamo da uno stato pulito


        ButtonMapPageConfig guestConfig = ButtonMapPageConfig.forGuestUser();
        favoritesManager = new FavoritesManager(null, errorLabel, guestConfig);
    }

    /**
     * Testa l'impossibilità per un ospite di aggiungere elementi ai preferiti.
     * Verifica che al click sul pulsante "stella", venga visualizzato un messaggio
     * di errore di colore rosso contenente la costante {@link Constants#LOGIN_REQUIRED_FAVORITES}.
     */
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


        favButton.doClick();

        assertTrue(errorLabel.isVisible());
        assertEquals(Constants.LOGIN_REQUIRED_FAVORITES, errorLabel.getText());
        assertEquals(java.awt.Color.RED, errorLabel.getForeground());
    }

    /**
     * Verifica l'iconografia del pulsante preferiti per gli utenti non loggati.
     * Controlla che il testo del pulsante contenga il codice HTML univoco
     * della stella vuota (☆), indicando che l'elemento non è (e non può essere) salvato.
     */
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


        assertTrue(favButton.getText().contains("9734"));
    }
}