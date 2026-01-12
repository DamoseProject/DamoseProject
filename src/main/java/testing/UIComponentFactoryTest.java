package testing;

import gui.UIComponentFactory;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link UIComponentFactory}.
 * Verifica che la fabbrica dei componenti generi elementi grafici conformi alle
 * specifiche di design dell'applicazione. Assicura la coerenza visiva testando
 * proprietà come cursori, colori di primo piano (foreground) e layout manager.
 */
class UIComponentFactoryTest {

    /**
     * Verifica la corretta creazione e stilizzazione dei pulsanti standard.
     * Assicura che il cursore cambi nell'icona "a manina" (HAND_CURSOR) per
     * migliorare la User Experience (UX) e che l'allineamento orizzontale sia centrato.
     */
    @Test
    @DisplayName("createStyledButton deve impostare il cursore a mano (HAND_CURSOR)")
    void testCreateStyledButton() {
        JButton button = UIComponentFactory.createStyledButton("Test");
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
        assertEquals(Component.CENTER_ALIGNMENT, button.getAlignmentX());
    }

    /**
     * Valida le proprietà delle etichette dedicate ai messaggi d'errore.
     * Verifica che la label sia inizialmente nascosta (per non occupare spazio visivo
     * inutilmente) e che il colore del testo sia rosso, rispettando le convenzioni
     * di interfaccia per i feedback negativi.
     */
    @Test
    @DisplayName("createErrorLabel deve essere inizialmente invisibile e di colore rosso")
    void testCreateErrorLabel() {
        JLabel label = UIComponentFactory.createErrorLabel();
        assertFalse(label.isVisible());
        assertEquals(Color.RED, label.getForeground());
    }

    /**
     * Verifica la configurazione strutturale dei pannelli verticali.
     * Controlla che il pannello utilizzi correttamente un {@link BoxLayout} orientato
     * lungo l'asse Y (verticale), permettendo l'impilamento dei componenti figli
     * mantenendo l'allineamento centrale.
     */
    @Test
    @DisplayName("createVerticalPanel deve utilizzare BoxLayout sull'asse Y")
    void testCreateVerticalPanel() {
        JPanel panel = UIComponentFactory.createVerticalPanel();
        assertTrue(panel.getLayout() instanceof BoxLayout);

        assertEquals(Component.CENTER_ALIGNMENT, panel.getAlignmentX());
    }
}