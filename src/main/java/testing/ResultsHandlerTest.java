package testing;

import gui.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultsHandlerTest {

    private ResultsHandler handler;
    private JPanel mockResultsPanel;
    private JLabel mockErrorLabel;
    private MapHandler mockMapManager;
    private ButtonMapPageConfig mockConfig;

    @BeforeEach
    void setUp() {
        // Mock delle dipendenze della classe
        mockResultsPanel = mock(JPanel.class);
        mockErrorLabel = mock(JLabel.class);
        mockMapManager = mock(MapHandler.class);
        mockConfig = mock(ButtonMapPageConfig.class);

        handler = new ResultsHandler(mockResultsPanel, mockErrorLabel, mockMapManager, mockConfig);
    }

    @Test
    void testShowResultsEmpty() throws SQLException {
        // Test con liste vuote: non deve crashare e deve aggiungere il messaggio "Nessun risultato"
        handler.showResults("Test", new ArrayList<>(), new ArrayList<>());

        // Verifichiamo che il pannello sia stato pulito
        verify(mockResultsPanel).removeAll();
        // Verifichiamo che sia stato aggiunto almeno un componente (header + no results)
        verify(mockResultsPanel, atLeast(2)).add(any(Component.class));
    }

    @Test
    void testSetResultsWithText() {
        // Test del metodo setResults che trasforma testo in righe grafiche
        String input = "Linea 1\nLinea 2";
        handler.setResults(input);

        verify(mockResultsPanel).removeAll();
        // Deve aggiungere 2 righe + 1 Glue (totale 3)
        verify(mockResultsPanel, times(3)).add(any(Component.class));
    }

    @Test
    void testShowFavoritesLoggedOut() {
        // Setup: simuliamo un utente non loggato tramite UserSession (Singleton)
        // Nota: UserSession andrebbe resettata per i test se possibile
        UserSession.getInstance().logout();

        handler.showFavorites();

        // Verifichiamo che compaia l'errore di login richiesto
        verify(mockErrorLabel).setText(Constants.LOGIN_REQUIRED_LIST);
        verify(mockErrorLabel).setVisible(true);
    }

    @Test
    void testInternalMapInitialization() {
        // Verifica con Reflection che la mappa degli expandedRows sia stata inizializzata
        Map<?, ?> expandedRows = getPrivateField(handler, "expandedRows", Map.class);
        assertNotNull(expandedRows);
        assertTrue(expandedRows.isEmpty());
    }

    @Test
    void testCreateGeneralRowLogic() throws Exception {
        // Poiché createGeneralRow è privato, lo testiamo indirettamente tramite setResults
        handler.setResults("Fermata 101");

        // Verifichiamo che il pannello sia stato rinfrescato graficamente
        verify(mockResultsPanel, atLeastOnce()).revalidate();
        verify(mockResultsPanel, atLeastOnce()).repaint();
    }

    /**
     * Utility reflection per accedere a campi privati (Standard di progetto)
     */
    private static <T> T getPrivateField(Object obj, String fieldName, Class<T> type) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(obj));
        } catch (Exception e) {
            throw new RuntimeException("Impossibile accedere al campo: " + fieldName, e);
        }
    }
}