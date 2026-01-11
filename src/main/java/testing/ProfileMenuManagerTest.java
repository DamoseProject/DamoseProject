package gui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class ProfileMenuManagerTest {

    private ProfileMenuManager profileManager;
    private MainFrame mockFrame;
    private UserSession session;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        profileManager = new ProfileMenuManager(null, mockFrame);
        session = UserSession.getInstance();
        session.login(1, "TestUser");
    }

    @Test
    @DisplayName("Il popup menu deve contenere le informazioni dell'utente e il tasto Esci")
    void testMenuStructure() {
        JPopupMenu menu = profileManager.createProfilePopupMenu(session);

        // Struttura: Label Utente, Label Email, Separator, MenuItem Logout
        assertEquals(4, menu.getComponentCount());

        JLabel nameLabel = (JLabel) menu.getComponent(0);
        assertTrue(nameLabel.getText().contains("TestUser"));

        JMenuItem logoutItem = (JMenuItem) menu.getComponent(3);
        assertEquals("Esci", logoutItem.getText());
    }

    @Test
    @DisplayName("Il logout deve pulire la sessione")
    void testLogoutAction() {
        JPopupMenu menu = profileManager.createProfilePopupMenu(session);
        JMenuItem logoutItem = (JMenuItem) menu.getComponent(3);

        // Simuliamo il click sul logout
        logoutItem.doClick();

        assertFalse(session.isLogged(), "La sessione dovrebbe essere chiusa dopo il logout");
    }
}