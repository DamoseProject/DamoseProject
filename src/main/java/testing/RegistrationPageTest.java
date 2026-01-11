package gui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationPageTest {

    private RegistrationPage registrationPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        registrationPage = new RegistrationPage(mockFrame);
    }

    @Test
    @DisplayName("I campi di registrazione devono essere inizialmente vuoti")
    void testInitialFieldsEmpty() {
        assertAll("Campi input registrazione",
                () -> assertEquals("", registrationPage.getUsernameRegistration()),
                () -> assertEquals("", registrationPage.getEmailRegistration()),
                () -> assertEquals("", registrationPage.getPasswordRegistration()),
                () -> assertEquals("", registrationPage.getConfirmPasswordRegistration())
        );
    }

    @Test
    @DisplayName("Verifica corretta gerarchia dei pannelli")
    void testPanelStructure() {
        JPanel main = registrationPage.getPanel();
        // NORTH: topPanel (con back button), CENTER: centerPanel (con i campi)
        assertTrue(main.getLayout() instanceof java.awt.BorderLayout);
        assertNotNull(main.getComponent(0));
        assertNotNull(main.getComponent(1));
    }
}