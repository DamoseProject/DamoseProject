package testing;

import gui.GeneralPanel;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link MainFrame}.
 * Verifica la corretta inizializzazione della finestra principale dell'applicazione,
 * validando le proprietà di sistema (titolo, operazioni di chiusura) e il
 * meccanismo di "View Switching" (cambio vista) che permette la navigazione
 * fluida tra le diverse schermate (Login, Registrazione, Mappa).
 */
class MainFrameTest {

    private MainFrame mainFrame;

    /**
     * Configura l'ambiente di test creando una nuova istanza di MainFrame
     * prima di ogni test. Sebbene Swing operi solitamente sull'Event Dispatch Thread,
     * i test unitari operano in modo sincrono per validare lo stato interno.
     */
    @BeforeEach
    void setUp() {
        mainFrame = new MainFrame();
    }

    /**
     * Valida le impostazioni fondamentali della finestra.
     * Verifica che il titolo dell'app sia corretto ("Damose!"), che la chiusura
     * termini effettivamente il processo e che la finestra si apra di default
     * in modalità massimizzata per una migliore esperienza utente.
     */
    @Test
    @DisplayName("Il frame deve essere inizializzato con le proprietà corrette")
    void testFrameInitialization() {
        assertAll("Proprietà Frame",
                () -> assertEquals("Damose!", mainFrame.getTitle()),
                () -> assertEquals(JFrame.EXIT_ON_CLOSE, mainFrame.getDefaultCloseOperation()),
                () -> assertEquals(JFrame.MAXIMIZED_BOTH, mainFrame.getExtendedState())
        );
    }

    /**
     * Verifica il funzionamento del motore di navigazione.
     * Utilizza un'implementazione "mock" dell'interfaccia {@link GeneralPanel}
     * per assicurarsi che il metodo setView sostituisca effettivamente il
     * componente visualizzato nel ContentPane del frame principale.
     */
    @Test
    @DisplayName("setView deve cambiare correttamente il ContentPane")
    void testSetView() {
        JPanel nuovoPannello = new JPanel();
        GeneralPanel mockPanel = () -> nuovoPannello;

        mainFrame.setView(mockPanel);
        assertEquals(nuovoPannello, mainFrame.getContentPane(), "Il contentPane non è stato aggiornato correttamente");
    }
}