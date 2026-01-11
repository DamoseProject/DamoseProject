package testing;

import gui.BackButton;
import gui.MainFrame;
import gui.RegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationPageTest {

    private MainFrame mockFrame;
    private RegistrationPage registrationPage;

    @BeforeEach
    void setUp() {
        // Mock del frame per evitare l'avvio della GUI reale e della connessione DB
        mockFrame = mock(MainFrame.class);
        registrationPage = new RegistrationPage(mockFrame);
    }

    @Test
    void testGettersFromFields() {
        // Accesso ai campi privati tramite Reflection
        JTextField usernameField = getPrivateField(registrationPage, "usernameField", JTextField.class);
        JTextField emailField = getPrivateField(registrationPage, "emailField", JTextField.class);
        JPasswordField passwordField = getPrivateField(registrationPage, "passwordField", JPasswordField.class);
        JPasswordField confirmPasswordField = getPrivateField(registrationPage, "confirmPasswordField", JPasswordField.class);

        // Simulazione inserimento dati
        usernameField.setText("MarioRossi");
        emailField.setText("mario@example.com");
        passwordField.setText("password123");
        confirmPasswordField.setText("password123");

        // Verifica tramite i metodi pubblici della classe
        assertEquals("MarioRossi", registrationPage.getUsernameRegistration());
        assertEquals("mario@example.com", registrationPage.getEmailRegistration());
        assertEquals("password123", registrationPage.getPasswordRegistration());
        assertEquals("password123", registrationPage.getConfirmPasswordRegistration());
    }

    @Test
    void testErrorLabelInitialState() {
        JLabel errorLabel = getPrivateField(registrationPage, "errorLabel", JLabel.class);

        // La label deve essere inizialmente invisibile
        assertFalse(errorLabel.isVisible(), "La label di errore non dovrebbe essere visibile all'inizio");
    }

    @Test
    void testBackButtonNavigation() {
        // Recuperiamo il topPanel per estrarre il BackButton
        JPanel topPanel = getPrivateField(registrationPage, "topPanel", JPanel.class);

        // Il BackButton è il primo componente nel BorderLayout.WEST del topPanel
        BackButton backButton = (BackButton) topPanel.getComponent(0);

        // Simuliamo il click sul pulsante indietro
        backButton.doClick();

        // Verifichiamo che il frame abbia ricevuto l'istruzione di cambiare vista (tornare al Login)
        verify(mockFrame, atLeastOnce()).setView(any());
    }

    /**
     * Utility reflection per accedere a campi privati (Standard di progetto)
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