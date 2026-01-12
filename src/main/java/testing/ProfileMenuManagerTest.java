package testing;

import gui.MainFrame;
import gui.ProfileMenuManager;
import gui.UserSession;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link ProfileMenuManager}.
 * Verifica la corretta generazione del menu contestuale del profilo utente.
 * Il test assicura che il menu mostri i dati corretti dell'utente loggato e
 * che l'azione di logout termini correttamente la sessione, garantendo
 * la sicurezza dei dati dell'account.
 */
class ProfileMenuManagerTest {

    private ProfileMenuManager profileManager;
    private MainFrame mockFrame;
    private UserSession session;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Inizializza il frame principale, il manager del profilo e forza
     * uno stato di login nella {@link UserSession} per permettere
     * la generazione del menu.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        profileManager = new ProfileMenuManager(null, mockFrame);
        session = UserSession.getInstance();
        session.login(1, "TestUser");
    }

    /**
     * Valida la struttura e il contenuto del menu a comparsa.
     * Verifica che il {@link JPopupMenu} contenga il numero corretto di componenti
     * (etichette informative, separatori e pulsanti di azione) e che il nome
     * dell'utente sia visualizzato correttamente.
     */
    @Test
    @DisplayName("Il popup menu deve contenere le informazioni dell'utente e il tasto Esci")
    void testMenuStructure() {
        JPopupMenu menu = profileManager.createProfilePopupMenu(session);


        assertEquals(4, menu.getComponentCount());

        JLabel nameLabel = (JLabel) menu.getComponent(0);
        assertTrue(nameLabel.getText().contains("TestUser"));

        JMenuItem logoutItem = (JMenuItem) menu.getComponent(3);
        assertEquals("Esci", logoutItem.getText());
    }

    /**
     * Testa la funzionalità di logout all'interno del menu.
     * Simulando il click programmatico sulla voce "Esci", il test verifica
     * che la {@link UserSession} venga resettata correttamente, impedendo
     * accessi non autorizzati successivi.
     */
    @Test
    @DisplayName("Il logout deve pulire la sessione")
    void testLogoutAction() {
        JPopupMenu menu = profileManager.createProfilePopupMenu(session);
        JMenuItem logoutItem = (JMenuItem) menu.getComponent(3);


        logoutItem.doClick();

        assertFalse(session.isLogged(), "La sessione dovrebbe essere chiusa dopo il logout");
    }
}