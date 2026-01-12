package testing;

import gui.GeneralPanel;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class MainFrameTest {

    private MainFrame mainFrame;

    @BeforeEach
    void setUp() {
        // SwingUtilities.invokeLater non è necessario qui perché JUnit gira nel main thread,
        // ma è bene testare la creazione dell'istanza.
        mainFrame = new MainFrame();
    }

    @Test
    @DisplayName("Il frame deve essere inizializzato con le proprietà corrette")
    void testFrameInitialization() {
        assertAll("Proprietà Frame",
                () -> assertEquals("Damose!", mainFrame.getTitle()),
                () -> assertEquals(JFrame.EXIT_ON_CLOSE, mainFrame.getDefaultCloseOperation()),
                () -> assertEquals(JFrame.MAXIMIZED_BOTH, mainFrame.getExtendedState())
        );
    }

    @Test
    @DisplayName("setView deve cambiare correttamente il ContentPane")
    void testSetView() {
        JPanel nuovoPannello = new JPanel();
        GeneralPanel mockPanel = () -> nuovoPannello;

        mainFrame.setView(mockPanel);
        assertEquals(nuovoPannello, mainFrame.getContentPane(), "Il contentPane non è stato aggiornato correttamente");
    }
}