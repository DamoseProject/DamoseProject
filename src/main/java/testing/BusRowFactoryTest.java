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

class BusRowFactoryTest {

    private BusRowFactory factory;

    @BeforeEach
    void setUp() {
        // Mocking minimale: passiamo null dove non strettamente necessario per questi test
        factory = new BusRowFactory(null, new JLabel(), new MapHandler(new JLabel()));
    }

    @Test
    @DisplayName("Dovrebbe creare una riga con evidenziazione gialla se isHighlighted è true")
    void testHighlightedRow() {
        // Creiamo l'oggetto usando il costruttore reale
        // Parametri: tripId, routeId, serviceId, textDestination, shortName, direction, arrivalTime, departureTime
        BusInUnaFermataRecord bus = new BusInUnaFermataRecord(
                "T1", "H", "S1", "Termini", "H", 0, "12:00:00", "12:00:00"
        );

        // Stop corretto (usando il costruttore della tua classe Stop)
        Stop stop = new Stop("1", "S01", "Test", 41.9f, 12.4f);

        JPanel row = factory.createBusRow(bus, stop, true);

        // Verifica il colore di highlight (255, 255, 200 è il giallo chiaro standard)
        assertEquals(new java.awt.Color(255, 255, 200), row.getBackground(),
                "Il colore di sfondo dovrebbe essere il giallo di highlight");
    }

    @Test
    @DisplayName("Verifica calcolo minuti rimanenti per Real-Time")
    void testRealTimeMinutesCalculation() {
        // Prepariamo un orario che sia esattamente tra 5 minuti da adesso
        LocalTime targetTime = LocalTime.now().plusMinutes(5);
        String timeStr = targetTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        BusInUnaFermataRecord bus = new BusInUnaFermataRecord(
                "T2", "64", "S1", "S. Pietro", "64", 0, timeStr, timeStr
        );
        bus.setRealTime(true);
        bus.setRitardoInSecondi(0); // Nessun ritardo, l'orario effettivo è quello statico

        Stop stop = new Stop("1", "S01", "Test", 41.9f, 12.4f);

        JPanel row = factory.createBusRow(bus, stop, false);

        // Cerchiamo la label che contiene il tempo (solitamente la prima o quella con il testo "min")
        // Questo dipende da come è implementata la tua createBusRow
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