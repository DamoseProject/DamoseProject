package testing;

import gui.BasePage;
import gui.MainFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class BasePageTest {

    private ConcreteBasePage basePage;

    // Classe concreta di supporto per il test
    private static class ConcreteBasePage extends BasePage {
        public ConcreteBasePage(MainFrame frame) {
            super(frame);
        }
    }

    @BeforeEach
    void setUp() {
        // Passiamo null per MainFrame per isolare il test della struttura grafica
        basePage = new ConcreteBasePage(null);
    }

    @Test
    @DisplayName("Il pannello principale dovrebbe essere inizializzato con BorderLayout")
    void testMainPanelInitialization() {
        JPanel panel = basePage.getPanel();
        assertNotNull(panel, "Il mainPanel non deve essere null");
        assertTrue(panel.getLayout() instanceof BorderLayout, "Il layout di default deve essere BorderLayout");
    }

    @Test
    @DisplayName("setLayout dovrebbe cambiare correttamente il layout del pannello")
    void testSetLayout() {
        LayoutManager newLayout = new FlowLayout();
        basePage.setLayout(newLayout);
        assertEquals(newLayout, basePage.getPanel().getLayout(), "Il layout dovrebbe essere stato aggiornato a FlowLayout");
    }

    @Test
    @DisplayName("createErrorLabel dovrebbe restituire una label rossa e inizialmente invisibile")
    void testCreateErrorLabel() {
        JLabel errorLabel = basePage.createErrorLabel();

        assertNotNull(errorLabel);
        assertEquals(Color.RED, errorLabel.getForeground(), "Il colore dell'errore deve essere rosso");
        assertFalse(errorLabel.isVisible(), "L'etichetta di errore deve essere invisibile di default");
        assertEquals(Component.CENTER_ALIGNMENT, errorLabel.getAlignmentX(), "L'allineamento deve essere centrato");
    }

    @Test
    @DisplayName("createFieldPanel dovrebbe aggregare correttamente label e componente")
    void testCreateFieldPanel() {
        JTextField textField = new JTextField(10);
        String labelText = "Username:";
        JPanel fieldPanel = basePage.createFieldPanel(labelText, textField);

        assertNotNull(fieldPanel);
        assertTrue(fieldPanel.getLayout() instanceof BoxLayout, "Il pannello del campo deve usare BoxLayout");

        // Verifichiamo che ci siano i due componenti (Label e TextField)
        Component[] components = fieldPanel.getComponents();
        assertTrue(components.length >= 2, "Il pannello deve contenere almeno label e campo");

        JLabel label = (JLabel) components[0];
        assertEquals(labelText, label.getText());
        assertEquals(Component.CENTER_ALIGNMENT, label.getAlignmentX());
    }
}