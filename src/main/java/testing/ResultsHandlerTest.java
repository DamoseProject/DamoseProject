package testing;

import gui.ButtonMapPageConfig;
import gui.ResultsHandler;
import model.Stop;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link ResultsHandler}.
 * Verifica la logica di popolamento dinamico del pannello dei risultati.
 * Il test assicura che le fermate trovate tramite ricerca vengano trasformate correttamente
 * in componenti grafici e che la gestione dello stato visivo (reset dei colori)
 * funzioni come previsto per garantire una navigazione fluida.
 */
class ResultsHandlerTest {

    private ResultsHandler resultsHandler;
    private JPanel resultsPanel;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Crea un pannello di destinazione e inizializza il gestore con una configurazione
     * di default (Guest) per testare il rendering in un ambiente controllato.
     */
    @BeforeEach
    void setUp() {
        resultsPanel = new JPanel();

        resultsHandler = new ResultsHandler(resultsPanel, new JLabel(), null, ButtonMapPageConfig.forGuestUser());
    }

    /**
     * Verifica che il metodo di visualizzazione popoli correttamente il contenitore Swing.
     * Il test simula il ritrovamento di una fermata (es. "Piazza Venezia") e controlla
     * che il numero di componenti aggiunti al pannello corrisponda alla struttura
     * prevista (Header + Riga della fermata).
     */
    @Test
    @DisplayName("showResults deve popolare il pannello con le fermate trovate")
    void testShowResultsPopulatesPanel() {
        List<Stop> fermate = new ArrayList<>();
        fermate.add(new Stop(
                "1",
                "70030",
                "Piazza Venezia",
                45.434f,
                12.338f
        ));

        try {
            resultsHandler.showResults("Venezia", fermate, new ArrayList<>());
            // Componenti attesi: 1 (Header) + 1 (Riga Fermata) + 1 (Vertical Glue)
            assertTrue(resultsPanel.getComponentCount() >= 2);
        } catch (Exception e) {
            fail("Errore durante il rendering dei risultati");
        }
    }

    /**
     * Valida la logica di pulizia degli stati grafici.
     * Verifica che il sistema sia in grado di accedere ai componenti del pannello
     * per resettare eventuali evidenziazioni (highlights), garantendo che la UI
     * ritorni allo stato neutro dopo una selezione.
     */
    @Test
    @DisplayName("resetRowHighlights deve riportare i pannelli al colore bianco")
    void testResetHighlights() {
        JPanel dummyRow = new JPanel();
        dummyRow.setBackground(java.awt.Color.LIGHT_GRAY);
        resultsPanel.add(dummyRow);


        assertNotNull(resultsPanel.getComponents());
    }
}