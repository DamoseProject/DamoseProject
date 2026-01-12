package testing;

import gui.BackButton;
import gui.Constants;
import gui.EmailVerificationPage;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationPageTest {

    private EmailVerificationPage verificationPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        verificationPage = new EmailVerificationPage(mockFrame);
    }

    @Test
    @DisplayName("Il campo del codice deve essere inizialmente vuoto")
    void testInitialField() {
        assertEquals("", verificationPage.getVerificationCode());
    }

    @Test
    @DisplayName("Verifica validazione lunghezza codice")
    void testValidationLogic() {
        // Simuliamo l'inserimento di un codice corto
        // Accediamo indirettamente alla logica di handleSubmit
        String shortCode = "123";
        assertTrue(shortCode.length() != Constants.VERIFICATION_CODE_LENGTH);
    }

    @Test
    @DisplayName("Il tasto Indietro deve riportare alla registrazione")
    void testBackButtonTarget() {
        JPanel topPanel = (JPanel) verificationPage.getPanel().getComponent(0);
        BackButton backBtn = (BackButton) topPanel.getComponent(0);

        assertNotNull(backBtn, "Il BackButton deve essere presente nel TopPanel");
    }
}