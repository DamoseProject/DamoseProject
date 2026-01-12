package testing;

import gui.MapHandler;
import model.Stop;
import org.junit.jupiter.api.*;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link MapHandler}.
 * Questa classe verifica l'integrità delle operazioni cartografiche, assicurando che
 * il motore di rendering della mappa (JXMapViewer) sia configurato correttamente
 * all'avvio e risponda con precisione ai comandi di navigazione verso specifiche fermate.
 */
class MapHandlerTest {

    private MapHandler mapHandler;
    private JLabel errorLabel;

    /**
     * Configura l'ambiente di test prima di ogni esecuzione.
     * Inizializza un'etichetta di errore fittizia e l'oggetto {@link MapHandler}
     * per operare in un ambiente isolato.
     */
    @BeforeEach
    void setUp() {
        errorLabel = new JLabel();
        mapHandler = new MapHandler(errorLabel);
    }

    /**
     * Verifica che la mappa sia inizializzata sulle coordinate di Roma.
     * Utilizza un'asserzione con tolleranza (delta) per confrontare le coordinate
     * di default del sistema con quelle geografiche reali della capitale.
     */
    @Test
    @DisplayName("Il MapViewer deve essere inizializzato su Roma di default")
    void testInitialLocation() {
        GeoPosition pos = mapHandler.getMapViewer().getAddressLocation();

        assertAll("Coordinate iniziali",
                () -> assertEquals(41.9, pos.getLatitude(), 0.1),
                () -> assertEquals(12.4, pos.getLongitude(), 0.1)
        );
    }

    /**
     * Valida la funzionalità di puntamento su una fermata specifica.
     * Assicura che invocando il metodo {@code showStopOnMap}, il centro della
     * visualizzazione della mappa coincida esattamente con la latitudine e
     * la longitudine dell'oggetto {@link Stop} passato come parametro.
     */
    @Test
    @DisplayName("showStopOnMap deve centrare la mappa sulla fermata")
    void testShowStopOnMap() {

        Stop stop = new Stop("1", "RM-123", "Termini", 41.901f, 12.501f);

        mapHandler.showStopOnMap(stop);

        GeoPosition newPos = mapHandler.getMapViewer().getAddressLocation();


        assertEquals(stop.getLatitude(), (float)newPos.getLatitude(), 0.001);
        assertEquals(stop.getLongitude(), (float)newPos.getLongitude(), 0.001);
    }
}