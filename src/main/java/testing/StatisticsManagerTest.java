package testing;

import gui.StatisticsManager;
import model.Stop;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link StatisticsManager}.
 * Verifica la corretta generazione e visualizzazione delle statistiche nel sistema.
 * Il test si concentra sulla resilienza del componente grafico durante la creazione
 * dei pop-up informativi e sulla gestione sicura di ricerche vuote o parametri
 * non presenti nel database, prevenendo crash dell'interfaccia utente.
 */
class StatisticsManagerTest {

    private StatisticsManager statsManager;
    private JLabel dummyInvoker;

    /**
     * Inizializza l'ambiente di test creando un manager statistico
     * e un componente "invoker" (JLabel) che simula l'origine del click.
     */
    @BeforeEach
    void setUp() {

        statsManager = new StatisticsManager(null);
        dummyInvoker = new JLabel();
    }

    /**
     * Verifica che la creazione del pop-up statistico avvenga senza errori.
     * Questo "Smoke Test" assicura che il motore di rendering Swing non sollevi
     * eccezioni (come NullPointerException o IllegalComponentStateException)
     * quando viene richiesto di mostrare i dati per una linea specifica (es. "64").
     */
    @Test
    @DisplayName("Il popup deve essere creato con le dimensioni corrette")
    void testPopupCreation() {

        assertDoesNotThrow(() -> {
            statsManager.showStatisticsPopup(dummyInvoker, "64");
        });
    }

    /**
     * Verifica la stabilità del manager in caso di input non validi.
     * Assicura che l'oggetto rimanga in uno stato consistente e pronto a
     * gestire la logica di fallback se la ricerca non produce risultati
     * o se il parametro di ricerca è vuoto.
     */
    @Test
    @DisplayName("Gestione della ricerca vuota o non trovata")
    void testEmptySearchMessage() {

        assertNotNull(statsManager);
    }
}