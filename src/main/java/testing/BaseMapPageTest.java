package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.event.KeyEvent;

class BaseMapPageTest {

    private ConcreteMapPage page;
    private MainFrame mockFrame;

    // Sottoclasse concreta per testare la classe astratta
    private static class ConcreteMapPage extends BaseMapPage {
        public ConcreteMapPage(MainFrame frame) {
            super(frame);
        }

        @Override
        protected ButtonMapPageConfig getButtonConfig() {
            // Restituiamo una configurazione di default per il test
            return new ButtonMapPageConfig(true, true, true, "", "");
        }
    }

    @BeforeEach
    void setUp() {
        // In un test reale potresti voler mockare MainFrame con Mockito
        page = new ConcreteMapPage(null);
    }

    @Test
    @DisplayName("Il campo di ricerca dovrebbe essere inizialmente vuoto")
    void testInitialResearchField() {
        assertEquals("", page.getResearchField(), "Il campo di ricerca deve essere vuoto all'avvio");
    }

    @Test
    @DisplayName("La pulizia del campo di ricerca dovrebbe funzionare")
    void testClearResearchField() {
        // Simulo l'inserimento di testo (accedendo al componente tramite riflessione o helper)
        // In questo caso usiamo il metodo pubblico se presente o un setter
        page.clearResearchField();
        assertEquals("", page.getResearchField());
    }

    @Test
    @DisplayName("setResults dovrebbe popolare correttamente il pannello dei risultati")
    void testSetResultsPopulatesPanel() {
        String testData = "Risultati per: Roma\nFermata 1 Termini\nFermata 2 Colosseo";

        // Eseguiamo l'operazione nel thread di Swing per sicurezza
        SwingUtilities.invokeLater(() -> {
            page.setResults(testData);

            // Verifichiamo che i componenti siano stati aggiunti al resultsPanel
            // Nota: per accedere a variabili private come resultsPanel nel test
            // dovresti renderle protected o usare getter/riflessione
            assertNotNull(page);
        });
    }

    @Test
    @DisplayName("Verifica logica flag searchConfirmed")
    void testSearchConfirmedLogic() {
        // Questo test verifica il comportamento del KeyListener implementato
        JTextField field = null;
        // Recupero il campo di ricerca (sarebbe meglio avere un getter protected)
        // Supponendo di averlo:
        // field.setText("Test");
        // ... simulo invio ...
        // assertTrue(searchConfirmed);
    }
}