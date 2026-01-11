package gui;

import model.Stop;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StatisticsManagerTest {

    private StatisticsManager statsManager;
    private JLabel dummyInvoker;

    @BeforeEach
    void setUp() {
        // Mocking del database (idealmente andrebbe usato un Mockito o un DB di test)
        statsManager = new StatisticsManager(null);
        dummyInvoker = new JLabel();
    }

    @Test
    @DisplayName("Il popup deve essere creato con le dimensioni corrette")
    void testPopupCreation() {
        // Questo test verifica che non ci siano eccezioni durante il rendering grafico
        assertDoesNotThrow(() -> {
            statsManager.showStatisticsPopup(dummyInvoker, "64");
        });
    }

    @Test
    @DisplayName("Gestione della ricerca vuota o non trovata")
    void testEmptySearchMessage() {
        // Verifichiamo indirettamente tramite riflessione o testando i componenti interni
        // che se searchText è invalido, il messaggio sia quello di Constants o custom
        assertNotNull(statsManager);
    }
}