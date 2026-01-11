package gui;

import model.BusInUnaFermataRecord;
import model.Stop;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class BusRowFactoryTest {

    private BusRowFactory factory;

    @BeforeEach
    void setUp() {
        // Mocking minimale o passaggio di null per i manager non critici nel test unitario
        factory = new BusRowFactory(null, new JLabel(), new MapHandler(new JLabel()));
    }

    @Test
    @DisplayName("Dovrebbe creare una riga con evidenziazione gialla se isHighlighted è true")
    void testHighlightedRow() {
        BusInUnaFermataRecord bus = new BusInUnaFermataRecord();
        bus.setRealTime(false);
        bus.setOrarioStatico(LocalTime.now().plusMinutes(10));
        bus.setRouteId("H");
        bus.setTextDestination("Termini");

        JPanel row = factory.createBusRow(bus, new Stop("1", "Test"), true);

        assertEquals(new java.awt.Color(255, 255, 200), row.getBackground(),
                "Il colore di sfondo dovrebbe essere il giallo di highlight");
    }

    @Test
    @DisplayName("Verifica calcolo minuti rimanenti per Real-Time")
    void testRealTimeMinutesCalculation() {
        BusInUnaFermataRecord bus = new BusInUnaFermataRecord();
        bus.setRealTime(true);
        // Impostiamo l'arrivo tra 5 minuti esatti
        bus.setOrarioEffettivo(LocalTime.now().plusMinutes(5));
        bus.setRouteId("64");
        bus.setTextDestination("S. Pietro");

        JPanel row = factory.createBusRow(bus, new Stop("1", "Test"), false);
        JLabel label = (JLabel) row.getComponent(0);

        assertTrue(label.getText().contains("5 min"),
                "La label dovrebbe mostrare '5 min' calcolati dinamicamente");
    }
}