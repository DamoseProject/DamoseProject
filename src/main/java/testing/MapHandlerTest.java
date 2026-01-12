package testing;

import gui.MapHandler;
import model.Stop;
import org.junit.jupiter.api.*;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

class MapHandlerTest {

    private MapHandler mapHandler;
    private JLabel errorLabel;

    @BeforeEach
    void setUp() {
        errorLabel = new JLabel();
        mapHandler = new MapHandler(errorLabel);
    }

    @Test
    @DisplayName("Il MapViewer deve essere inizializzato su Roma di default")
    void testInitialLocation() {
        GeoPosition pos = mapHandler.getMapViewer().getAddressLocation();
        // Verifica coordinate approssimative di Roma
        assertAll("Coordinate iniziali",
                () -> assertEquals(41.9, pos.getLatitude(), 0.1),
                () -> assertEquals(12.4, pos.getLongitude(), 0.1)
        );
    }

    @Test
    @DisplayName("showStopOnMap deve centrare la mappa sulla fermata")
    void testShowStopOnMap() {
        // Crea l'oggetto Stop usando il costruttore della classe stop:
        // id, code, name, latitude, longitude
        Stop stop = new Stop("1", "RM-123", "Termini", 41.901f, 12.501f);

        mapHandler.showStopOnMap(stop);

        GeoPosition newPos = mapHandler.getMapViewer().getAddressLocation();

        // Usiamo un delta di 0.001 perché confrontiamo float e double
        assertEquals(stop.getLatitude(), (float)newPos.getLatitude(), 0.001);
        assertEquals(stop.getLongitude(), (float)newPos.getLongitude(), 0.001);
    }
}