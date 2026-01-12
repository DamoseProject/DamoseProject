package testing;

import gui.BusRowFactory;
import gui.MapHandler;
import model.BusInUnaFermataRecord;
import model.Stop;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link BusRowFactory}.
 * Verifica la corretta generazione grafica delle righe dei bus in arrivo.
 * Il test si concentra sulla precisione del calcolo del tempo residuo visualizzato
 * all'utente e sulla corretta applicazione degli stili grafici (highlighting)
 * basati sullo stato della ricerca.
 */
class BusRowFactoryTest {

    private BusRowFactory factory;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Viene inizializzata la factory con oggetti di supporto (mock) minimi necessari
     * per evitare NullPointerException durante la creazione dei componenti Swing.
     */
    @BeforeEach
    void setUp() {
        factory = new BusRowFactory(null, new JLabel(), new MapHandler(new JLabel()));
    }

    /**
     * Verifica che la riga venga renderizzata con uno sfondo differente (giallo)
     * quando l'utente seleziona una specifica linea bus.
     * Questo test garantisce che il feedback visivo della ricerca funzioni correttamente.
     */
    @Test
    @DisplayName("Dovrebbe creare una riga con evidenziazione gialla se isHighlighted è true")
    void testHighlightedRow() {
        BusInUnaFermataRecord bus = new BusInUnaFermataRecord(
                "T1", "H", "S1", "Termini", "H", 0, "12:00:00", "12:00:00"
        );


        Stop stop = new Stop("1", "S01", "Test", 41.9f, 12.4f);

        JPanel row = factory.createBusRow(bus, stop, true);

        // Verifica il colore di highlight (255, 255, 200 è il giallo chiaro standard)
        assertEquals(new java.awt.Color(255, 255, 200), row.getBackground(),
                "Il colore di sfondo dovrebbe essere il giallo di highlight");
    }

    /**
     * Valida il calcolo dinamico dei minuti mancanti all'arrivo.
     * Il test crea un transito programmato tra esattamente 5 minuti e verifica
     * che la label generata riporti correttamente la stringa "5 min".
     * Questo assicura che la logica di calcolo temporale sia sincronizzata con l'orario di sistema.
     */
    @Test
    @DisplayName("Verifica calcolo minuti rimanenti per Real-Time")
    void testRealTimeMinutesCalculation() {
        LocalTime targetTime = LocalTime.now().plusMinutes(5);
        String timeStr = targetTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        BusInUnaFermataRecord bus = new BusInUnaFermataRecord(
                "T2", "64", "S1", "S. Pietro", "64", 0, timeStr, timeStr
        );
        bus.setRealTime(true);
        bus.setRitardoInSecondi(0);

        Stop stop = new Stop("1", "S01", "Test", 41.9f, 12.4f);

        JPanel row = factory.createBusRow(bus, stop, false);


        boolean foundMinText = false;
        for (java.awt.Component c : row.getComponents()) {
            if (c instanceof JLabel) {
                String text = ((JLabel) c).getText();
                if (text != null && text.contains("5 min")) {
                    foundMinText = true;
                    break;
                }
            }
        }

        assertTrue(foundMinText, "La riga dovrebbe contenere una label con '5 min'");
    }
}