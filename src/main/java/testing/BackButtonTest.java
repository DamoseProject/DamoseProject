package testing;


import gui.BackButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per il componente {@link BackButton}.
 * Utilizza JUnit 5 per garantire che il pulsante di navigazione rispetti
 * i requisiti grafici e funzionali, in particolare l'esecuzione del
 * comando di ritorno alla pagina precedente.
 */
class BackButtonTest {

    /**
     * Verifica che il pulsante esegua correttamente l'azione definita.
     * Viene utilizzato un array di boolean come "wrapper" per catturare
     * il cambiamento di stato all'interno di una lambda expression, simulando
     * il comportamento del navigatore delle pagine.
     */
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

    /**
     * Verifica che l'etichetta testuale del pulsante sia corretta.
     * Questo garantisce la coerenza dell'interfaccia utente (UI) per la localizzazione.
     */
    @Test
    @DisplayName("Il testo del bottone deve essere 'Indietro'")
    void testButtonText() {
        BackButton backButton = new BackButton(null, () -> {});
        assertEquals("Indietro", backButton.getText());
    }
}