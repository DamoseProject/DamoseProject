package testing;

import gui.GeneralPanel;
import gui.MainFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class MainFrameTest {

    private MainFrame mainFrame;

    @BeforeEach
    void setUp() {
        // Inizializziamo il frame.
        // Nota: se il DB non è attivo, il costruttore potrebbe bloccare il test.
        mainFrame = new MainFrame();
    }

    @Test
    void testMainFrameInitialization() {
        // Verifica che il titolo sia corretto
        assertEquals("Damose!", mainFrame.getTitle(), "Il titolo del frame non è corretto");

        // Verifica che l'operazione di chiusura sia quella impostata
        assertEquals(JFrame.EXIT_ON_CLOSE, mainFrame.getDefaultCloseOperation());

        // Verifica che la finestra sia visibile
        assertTrue(mainFrame.isVisible(), "Il frame dovrebbe essere visibile");
    }

    @Test
    void testSetViewChangesContent() {
        // Creiamo un pannello finto per testare il cambio vista
        // Assumiamo che GeneralPanel sia un'interfaccia o una classe base esistente
        JPanel testPanel = new JPanel();
        GeneralPanel mockView = () -> testPanel;

        // Eseguiamo il cambio vista
        mainFrame.setView(mockView);

        // Verifichiamo che il ContentPane del frame sia quello che abbiamo passato
        assertEquals(testPanel, mainFrame.getContentPane(), "Il pannello non è stato sostituito correttamente");
    }
}