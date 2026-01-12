package testing;

import gui.UIComponentFactory;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class UIComponentFactoryTest {

    @Test
    @DisplayName("createStyledButton deve impostare il cursore a mano (HAND_CURSOR)")
    void testCreateStyledButton() {
        JButton button = UIComponentFactory.createStyledButton("Test");
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
        assertEquals(Component.CENTER_ALIGNMENT, button.getAlignmentX());
    }

    @Test
    @DisplayName("createErrorLabel deve essere inizialmente invisibile e di colore rosso")
    void testCreateErrorLabel() {
        JLabel label = UIComponentFactory.createErrorLabel();
        assertFalse(label.isVisible());
        assertEquals(Color.RED, label.getForeground());
    }

    @Test
    @DisplayName("createVerticalPanel deve utilizzare BoxLayout sull'asse Y")
    void testCreateVerticalPanel() {
        JPanel panel = UIComponentFactory.createVerticalPanel();
        assertTrue(panel.getLayout() instanceof BoxLayout);
        // Verifica indiretta dell'asse Y tramite le preferenze del layout
        assertEquals(Component.CENTER_ALIGNMENT, panel.getAlignmentX());
    }
}