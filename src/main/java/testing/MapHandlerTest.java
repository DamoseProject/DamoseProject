package gui;

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
        Stop stop = new Stop("1", "Termini");
        stop.setLatitude(41.901);
        stop.setLongitude(12.501);

        mapHandler.showStopOnMap(stop);

        GeoPosition newPos = mapHandler.getMapViewer().getAddressLocation();
        assertEquals(stop.getLatitude(), newPos.getLatitude());
        assertEquals(stop.getLongitude(), newPos.getLongitude());
    }

    @Test
    @DisplayName("Il setup dello zoom da tastiera deve registrare le Action")
    void testKeyboardZoomActions() {
        mapHandler.setupKeyboardZoom();
        ActionMap actionMap = mapHandler.getMapViewer().getActionMap();

        assertNotNull(actionMap.get("zoomIn"));
        assertNotNull(actionMap.get("zoomOut"));
    }
}