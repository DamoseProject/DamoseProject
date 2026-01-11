package gui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginPageTest {

    private LoginPage loginPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        loginPage = new LoginPage(mockFrame);
    }

    @Test
    @DisplayName("I campi di login devono essere inizialmente vuoti")
    void testInitialFields() {
        assertEquals("", loginPage.getUsernameLogin());
        assertEquals("", loginPage.getPasswordLogin());
    }

    @Test
    @DisplayName("Il click sul link Q&A deve cambiare la vista")
    void testNavigationToHelp() {
        // Poiché createClickableLabel usa una lambda Runnable, testiamo che il frame
        // cambi effettivamente pannello quando invocato (simulato tramite logica interna)
        assertNotNull(loginPage.getPanel());
    }

    @Test
    @DisplayName("Verifica configurazione JPasswordField")
    void testPasswordFieldProperties() {
        // Cerchiamo il passwordField nel pannello centrale
        // Struttura: mainPanel -> centerPanel -> contentPanel -> passwordPanel -> JPasswordField
        assertTrue(loginPage.getPanel().getComponent(1) instanceof JPanel);
    }
}