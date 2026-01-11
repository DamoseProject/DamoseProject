package testing;

import gui.LoginPage;
import gui.MainFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginPageTest {

    private MainFrame mockFrame;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        // Usiamo Mockito per evitare l'avvio della connessione DB e della GUI reale
        mockFrame = mock(MainFrame.class);
        loginPage = new LoginPage(mockFrame);
    }

    @Test
    void testGettersFromFields() {
        // Accesso ai campi privati tramite la tua utility
        JTextField usernameField = getPrivateField(loginPage, "usernameField", JTextField.class);
        JPasswordField passwordField = getPrivateField(loginPage, "passwordField", JPasswordField.class);

        usernameField.setText("Mario");
        passwordField.setText("12345");

        assertEquals("Mario", loginPage.getUsernameLogin());
        assertEquals("12345", loginPage.getPasswordLogin());
    }

    @Test
    void testErrorLabelInitialState() {
        JLabel errorLabel = getPrivateField(loginPage, "errorAccessLabel", JLabel.class);

        // Verifica che inizialmente l'errore sia nascosto (o vuoto)
        assertFalse(errorLabel.isVisible(), "La label di errore dovrebbe essere inizialmente invisibile");
    }

    @Test
    void testInfoButtonNavigation() {
        // Recuperiamo il topPanel e cerchiamo il bottone "Info"
        JPanel topPanel = getPrivateField(loginPage, "topPanel", JPanel.class);

        // Navighiamo la gerarchia (TopPanel -> LeftPanel -> InfoButton)
        JPanel leftPanel = (JPanel) topPanel.getComponent(0);
        JButton infoButton = (JButton) leftPanel.getComponent(0);

        // Simuliamo il click
        infoButton.doClick();

        // Verifichiamo che il frame abbia ricevuto il comando di cambiare vista
        verify(mockFrame, atLeastOnce()).setView(any());
    }

    /**
     * Utility reflection per accedere a campi privati (Standard richiesto)
     */
    private static <T> T getPrivateField(Object obj, String fieldName, Class<T> type) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(obj));
        } catch (Exception e) {
            throw new RuntimeException("Impossibile accedere al campo: " + fieldName, e);
        }
    }
}