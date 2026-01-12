package testing;

import gui.HelpPage;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link HelpPage}.
 * Verifica la corretta disposizione dei pannelli informativi e l'accuratezza
 * delle istruzioni fornite all'utente. Assicura che la guida rapida contenga
 * tutte le parole chiave essenziali per il funzionamento del sistema (zoom, preferiti, refresh).
 */
class HelpPageTest {

    private HelpPage helpPage;
    private MainFrame mockFrame;

    /**
     * Inizializza l'ambiente di test creando un frame principale e
     * un'istanza della pagina di aiuto prima di ogni metodo di test.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        helpPage = new HelpPage(mockFrame);
    }

    /**
     * Valida la struttura architettonica della pagina.
     * Verifica che il pannello principale utilizzi un {@link BorderLayout} e che
     * le aree NORTH (per la navigazione) e CENTER (per i contenuti) siano
     * state popolate correttamente.
     */
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

    /**
     * Esegue un'ispezione testuale del contenuto informativo della pagina.
     * Il test naviga nella gerarchia dei componenti per estrarre la label di aiuto
     * e verifica, tramite asserzioni multiple, la presenza di termini critici
     * necessari all'utente per interagire con la mappa e i dati real-time.
     */
    @Test
    @DisplayName("Il testo di aiuto deve contenere informazioni sullo zoom e i preferiti")
    void testHelpTextContent() {

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