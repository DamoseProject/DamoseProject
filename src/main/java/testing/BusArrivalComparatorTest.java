package testing;

import gui.BusArrivalComparator;
import model.BusInUnaFermataRecord;
import org.junit.jupiter.api.*;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link BusArrivalComparator}.
 * Verifica che la logica di ordinamento dei bus in arrivo rispetti sia i criteri
 * di orario (cronologici) sia quelli di affidabilità del dato (priorità).
 * * <p>La gerarchia di ordinamento testata prevede:</p>
 * <ul>
 * <li>1. Bus con dati Real-Time (GPS attivo)</li>
 * <li>2. Bus con Smart Prediction (stima basata su storico)</li>
 * <li>3. Bus con dati puramente Statici (tabella oraria)</li>
 * </ul>
 */
class BusArrivalComparatorTest {

    private BusArrivalComparator comparator;

    /**
     * Inizializza il comparatore prima di ogni test.
     */
    @BeforeEach
    void setUp() {
        comparator = new BusArrivalComparator();
    }

    /**
     * Verifica il principio di precedenza della qualità del dato.
     * Un bus monitorato via GPS (Real-Time) deve apparire prima in lista rispetto
     * a uno statico, anche se l'orario di arrivo previsto è temporalmente successivo,
     * per garantire all'utente l'informazione più affidabile in cima.
     */
    @Test
    @DisplayName("Il bus Real-Time deve avere la precedenza su quello Statico anche se arriva dopo")
    void testPriorityOverTime() {
        // Bus 1: Real-Time alle 10:15 (Priorità 1)
        // Creiamo uno stop alle 10:15:00
        BusInUnaFermataRecord busRT = new BusInUnaFermataRecord(
                "T1", "64", "S1", "Termini", "64", 0, "10:15:00", "10:15:00"
        );
        busRT.setRealTime(true);

        // Bus 2: Statico alle 10:10 (Priorità 3)
        BusInUnaFermataRecord busStatic = new BusInUnaFermataRecord(
                "T2", "H", "S1", "S. Pietro", "H", 0, "10:10:00", "10:10:00"
        );
        busStatic.setRealTime(false);
        busStatic.setIsSmartPredicted(false);

        // Il comparator restituisce un valore negativo se il primo elemento è "minore" (viene prima) del secondo.
        // Poiché Priorità 1 < Priorità 3, busRT deve venire prima di busStatic.
        assertTrue(comparator.compare(busRT, busStatic) < 0,
                "Il bus Real-Time dovrebbe venire prima di quello statico per priorità");
    }

    /**
     * Verifica che, a parità di affidabilità (es. entrambi Real-Time),
     * i bus vengano ordinati correttamente in base all'orario di arrivo.
     */
    @Test
    @DisplayName("A parità di priorità, l'ordine deve essere cronologico")
    void testChronologicalOrder() {
        // BusEarly: 09:00:00
        BusInUnaFermataRecord busEarly = new BusInUnaFermataRecord(
                "T1", "8", "S1", "Casaletto", "8", 0, "09:00:00", "09:00:00"
        );
        busEarly.setRealTime(true);

        // BusLate: 09:30:00
        BusInUnaFermataRecord busLate = new BusInUnaFermataRecord(
                "T2", "8", "S1", "Venezia", "8", 0, "09:30:00", "09:30:00"
        );
        busLate.setRealTime(true);

        assertTrue(comparator.compare(busEarly, busLate) < 0,
                "A parità di Real-Time, il bus delle 09:00 dovrebbe venire prima di quello delle 09:30");
    }

    /**
     * Verifica l'impatto del ritardo rilevato sull'ordinamento.
     * Un bus programmato prima ma che accumula ritardo deve "slittare"
     * correttamente sotto un bus programmato dopo ma che risulta più imminente.
     */
    @Test
    @DisplayName("Verifica ordinamento con ritardo")
    void testDelayImpact() {
        // Bus 1: Statico 10:00 + 600 secondi (10 min) ritardo = 10:10 effettivo
        BusInUnaFermataRecord busDelayed = new BusInUnaFermataRecord(
                "T1", "40", "S1", "Termini", "40", 0, "10:00:00", "10:00:00"
        );
        busDelayed.setRealTime(true);
        busDelayed.setRitardoInSecondi(600);

        // Bus 2: Statico 10:05 + 0 ritardo = 10:05 effettivo
        BusInUnaFermataRecord busOnTime = new BusInUnaFermataRecord(
                "T2", "40", "S1", "Termini", "40", 0, "10:05:00", "10:05:00"
        );
        busOnTime.setRealTime(true);

        // Entrambi Real-Time, quindi decide l'orario effettivo. 10:05 < 10:10.
        assertTrue(comparator.compare(busOnTime, busDelayed) < 0,
                "Il bus delle 10:05 dovrebbe apparire prima di quello delle 10:00 che ha 10 min di ritardo");
    }
}