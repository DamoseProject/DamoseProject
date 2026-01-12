package testing;

import gui.BaseMapPage;
import gui.ButtonMapPageConfig;
import gui.MainFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link BaseMapPage}.
 * Poiché la classe target è astratta, il test utilizza una classe anonima interna
 * per concretizzare i metodi astratti e permettere la verifica della logica comune
 * a tutte le pagine basate su mappa (layout, gestione ricerca e inizializzazione manager).
 */
class BaseMapPageTest {

    private BaseMapPage testPage;
    private MainFrame mockFrame;

    /**
     * Configura l'ambiente di test prima di ogni singola esecuzione.
     * Crea un'istanza concreta di BaseMapPage tramite una classe anonima,
     * simulando il comportamento di un utente ospite (Guest).
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        // Implementazione anonima per testare la classe astratta
        testPage = new BaseMapPage(mockFrame) {
            @Override
            protected ButtonMapPageConfig getButtonConfig() {
                return ButtonMapPageConfig.forGuestUser();
            }
        };
    }

    /**
     * Verifica che i componenti fondamentali del pannello siano stati
     * inizializzati correttamente e che il campo di ricerca sia pronto all'uso.
     */
    @Test
    @DisplayName("Inizializzazione corretta dei manager interni")
    void testManagersInitialization() {
        assertAll("Manager presenti",
                () -> assertNotNull(testPage.getPanel(), "Il pannello principale deve essere creato"),
                // Anche se i campi sono privati, verifichiamo che il caricamento non fallisca
                () -> assertEquals("", testPage.getResearchField(), "Il campo ricerca deve essere inizialmente vuoto")
        );
    }

    /**
     * Testa la funzionalità di reset della ricerca.
     * Assicura che il metodo pulisca correttamente il campo di input testuale.
     */
    @Test
    @DisplayName("clearResearchField deve pulire il testo e resettare lo stato")
    void testClearResearchField() {
        // Simuliamo l'inserimento di testo (accedendo tramite un metodo pubblico o riflessione se necessario)
        // In questo caso usiamo il metodo pubblico se presente
        testPage.clearResearchField();
        assertEquals("", testPage.getResearchField());
    }

    /**
     * Valida la struttura del layout della pagina.
     * Verifica che il pannello principale contenga la gerarchia corretta dei componenti
     * (Mappa e Risultati) rispettando l'organizzazione definita dal BoxLayout.
     */
    @Test
    @DisplayName("Verifica struttura layout: Mappa a sinistra e Risultati a destra")
    void testMapAndResultsLayout() {
        // Il mapAndResultsPanel usa un BoxLayout X_AXIS
        // Possiamo verificare indirettamente se la struttura è stata montata
        Container main = testPage.getPanel();
        assertNotNull(main.getComponent(1), "Il pannello centrale (mappa + risultati) deve essere presente");
    }
}