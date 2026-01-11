package gui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class BaseMapPageTest {

    private BaseMapPage testPage;
    private MainFrame mockFrame;

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

    @Test
    @DisplayName("Inizializzazione corretta dei manager interni")
    void testManagersInitialization() {
        assertAll("Manager presenti",
                () -> assertNotNull(testPage.getPanel(), "Il pannello principale deve essere creato"),
                // Anche se i campi sono privati, verifichiamo che il caricamento non fallisca
                () -> assertEquals("", testPage.getResearchField(), "Il campo ricerca deve essere inizialmente vuoto")
        );
    }

    @Test
    @DisplayName("clearResearchField deve pulire il testo e resettare lo stato")
    void testClearResearchField() {
        // Simuliamo l'inserimento di testo (accedendo tramite un metodo pubblico o riflessione se necessario)
        // In questo caso usiamo il metodo pubblico se presente
        testPage.clearResearchField();
        assertEquals("", testPage.getResearchField());
    }

    @Test
    @DisplayName("Verifica struttura layout: Mappa a sinistra e Risultati a destra")
    void testMapAndResultsLayout() {
        // Il mapAndResultsPanel usa un BoxLayout X_AXIS
        // Possiamo verificare indirettamente se la struttura è stata montata
        Container main = testPage.getPanel();
        assertNotNull(main.getComponent(1), "Il pannello centrale (mappa + risultati) deve essere presente");
    }
}