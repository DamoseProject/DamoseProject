package gui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class BasePageTest {

    private BasePage testPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        // Creiamo un'istanza concreta minima per il test
        testPage = new BasePage(mockFrame) {};
    }

    @Test
    @DisplayName("Il mainPanel deve essere inizializzato con BorderLayout")
    void testInitialPanel() {
        assertNotNull(testPage.getPanel());
        assertTrue(testPage.getPanel().getLayout() instanceof BorderLayout);
    }

    @Test
    @DisplayName("createFieldPanel deve allineare correttamente label e campo")
    void testCreateFieldPanel() {
        JTextField field = new JTextField();
        JPanel panel = testPage.createFieldPanel("Nome:", field);

        assertEquals(2, panel.getComponentCount(), "Il pannello dovrebbe avere una label e un campo");

        JLabel label = (JLabel) panel.getComponent(0);
        assertEquals("Nome:", label.getText());
        assertEquals(Component.CENTER_ALIGNMENT, label.getAlignmentX());
    }

    @Test
    @DisplayName("showError deve rendere visibile la label e impostare il messaggio")
    void testShowError() {
        JLabel errorLabel = new JLabel();
        errorLabel.setVisible(false);

        testPage.showError(errorLabel, "Errore di test");

        assertTrue(errorLabel.isVisible());
        assertEquals("Errore di test", errorLabel.getText());
    }

    @Test
    @DisplayName("createTopPanelWithBackButton deve bilanciare il layout con uno Strut a destra")
    void testTopPanelBalance() {
        JPanel topPanel = testPage.createTopPanelWithBackButton("Titolo", PageType.LOGIN);
        BorderLayout layout = (BorderLayout) topPanel.getLayout();

        assertAll("Verifica componenti TopPanel",
                () -> assertTrue(topPanel.getComponent(0) instanceof BackButton, "Deve esserci un BackButton a Ovest"),
                () -> assertTrue(topPanel.getComponent(1) instanceof JLabel, "Deve esserci un Titolo al Centro"),
                () -> assertTrue(topPanel.getComponent(2) instanceof Box.Filler, "Deve esserci uno spazio vuoto a Est per bilanciare")
        );
    }
}