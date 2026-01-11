package gui;

import model.BusInUnaFermataRecord;
import org.junit.jupiter.api.*;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class BusArrivalComparatorTest {

    private BusArrivalComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new BusArrivalComparator();
    }

    @Test
    @DisplayName("Il bus Real-Time deve avere la precedenza su quello Statico anche se arriva dopo")
    void testPriorityOverTime() {
        // Bus 1: Real-Time alle 10:15
        BusInUnaFermataRecord busRT = new BusInUnaFermataRecord();
        busRT.setRealTime(true);
        busRT.setOrarioEffettivo(LocalTime.of(10, 15));

        // Bus 2: Statico alle 10:10
        BusInUnaFermataRecord busStatic = new BusInUnaFermataRecord();
        busStatic.setRealTime(false);
        busStatic.setIsSmartPredicted(false);
        busStatic.setOrarioStatico(LocalTime.of(10, 10));

        // Il comparator deve restituire un valore negativo (busRT < busStatic in termini di ordinamento)
        assertTrue(comparator.compare(busRT, busStatic) < 0);
    }

    @Test
    @DisplayName("A parità di priorità, l'ordine deve essere cronologico")
    void testChronologicalOrder() {
        BusInUnaFermataRecord busEarly = new BusInUnaFermataRecord();
        busEarly.setRealTime(true);
        busEarly.setOrarioEffettivo(LocalTime.of(09, 00));

        BusInUnaFermataRecord busLate = new BusInUnaFermataRecord();
        busLate.setRealTime(true);
        busLate.setOrarioEffettivo(LocalTime.of(09, 30));

        assertTrue(comparator.compare(busEarly, busLate) < 0);
    }
}