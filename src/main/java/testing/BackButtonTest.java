package testing;

import gui.BackButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BackButtonTest {

    @Test
    @DisplayName("Il bottone deve eseguire l'azione passata quando cliccato")
    void testActionButton() {
        // Usiamo un array di boolean per "catturare" l'esecuzione della lambda
        boolean[] actionExecuted = {false};

        // Creiamo il bottone passando una lambda che cambia il boolean
        BackButton backButton = new BackButton(null, () -> actionExecuted[0] = true);

        // Simuliamo il click
        backButton.doClick();

        assertTrue(actionExecuted[0], "L'azione passata al costruttore non è stata eseguita");
    }

    @Test
    @DisplayName("Il testo del bottone deve essere 'Indietro'")
    void testButtonText() {
        BackButton backButton = new BackButton(null, () -> {});
        assertEquals("Indietro", backButton.getText());
    }
}