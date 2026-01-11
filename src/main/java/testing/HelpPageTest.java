package gui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class HelpPageTest {

    private HelpPage helpPage;
    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        helpPage = new HelpPage(mockFrame);
    }

    @Test
    @DisplayName("Il layout deve contenere il TopPanel e il CenterPanel")
    void testLayoutStructure() {
        JPanel main = helpPage.getPanel();
        BorderLayout layout = (BorderLayout) main.getLayout();

        assertAll("Verifica componenti principali",
                () -> assertNotNull(layout.getLayoutComponent(BorderLayout.NORTH), "TopPanel mancante"),
                () -> assertNotNull(layout.getLayoutComponent(BorderLayout.CENTER), "CenterPanel mancante")
        );
    }

    @Test
    @DisplayName("Il testo di aiuto deve contenere informazioni sullo zoom e i preferiti")
    void testHelpTextContent() {
        // Cerchiamo la JLabel nel pannello centrale
        JPanel center = (JPanel) helpPage.getPanel().getComponent(1);
        JLabel helpLabel = (JLabel) center.getComponent(1); // 0: Glue, 1: Label

        String text = helpLabel.getText();
        assertAll("Contenuto Informativo",
                () -> assertTrue(text.contains("zoom"), "Deve spiegare lo zoom"),
                () -> assertTrue(text.contains("Preferiti"), "Deve menzionare i preferiti"),
                () -> assertTrue(text.contains("refresh"), "Deve spiegare l'aggiornamento dati")
        );
    }
}