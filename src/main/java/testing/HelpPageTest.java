package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpPageTest {

    private MainFrame mockFrame;
    private HelpPage helpPage;

    @BeforeEach
    void setUp() {
        // Mock del frame per isolare la pagina dal sistema reale
        mockFrame = mock(MainFrame.class);
        helpPage = new HelpPage(mockFrame);
    }

    @Test
    void testHelpTextIsCorrect() {
        // Recuperiamo il centerPanel tramite reflection
        JPanel centerPanel = getPrivateField(helpPage, "centerPanel", JPanel.class);

        // Il secondo componente del centerPanel (dopo il VerticalGlue) è la JLabel
        JLabel helpLabel = (JLabel) centerPanel.getComponent(1);

        String text = helpLabel.getText();

        // Verifichiamo che contenga le parole chiave fondamentali della guida
        assertNotNull(text);
        assertTrue(text.contains("zoom sulla mappa"), "Il testo di aiuto non contiene informazioni sullo zoom");
        assertTrue(text.contains("lista Preferiti"), "Il testo di aiuto non contiene informazioni sui preferiti");
        assertTrue(text.contains("registrazione"), "Il testo di aiuto non contiene informazioni sulla registrazione");
    }

    @Test
    void testBackButtonNavigation() {
        // Recuperiamo il topPanel tramite reflection
        JPanel topPanel = getPrivateField(helpPage, "topPanel", JPanel.class);

        // Il BackButton è aggiunto in BorderLayout.WEST (posizione 0 nel layout)
        BackButton backButton = (BackButton) topPanel.getComponent(0);

        // Simuliamo il click
        backButton.doClick();

        // Verifichiamo che il frame abbia ricevuto il comando di cambiare vista per tornare al Login
        verify(mockFrame, atLeastOnce()).setView(any());
    }

    @Test
    void testComponentsPresence() {
        // Verifichiamo che i pannelli principali siano stati inizializzati
        assertNotNull(getPrivateField(helpPage, "topPanel", JPanel.class));
        assertNotNull(getPrivateField(helpPage, "centerPanel", JPanel.class));
    }

    /**
     * Utility reflection per accedere a campi privati (Standard di progetto)
     */
    private static <T> T getPrivateField(Object obj, String fieldName, Class<T> type) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(obj));
        } catch (Exception e) {
            throw new RuntimeException("Impossibile accedere al campo: " + fieldName, e);
        }
    }
}