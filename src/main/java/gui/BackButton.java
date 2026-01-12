package gui;

import javax.swing.*;

/**
 * Questa classe crea il pulsante "Indietro".
 * Serve per tornare alla pagina precedente senza dover riscrivere
 * ogni volta il codice del pulsante in tutte le schermate.
 * *
 */
public class BackButton extends JButton {

    /**
     * Crea il pulsante e imposta cosa deve succedere quando lo clicchi.
     * * @param frame Il frame principale dell'app (serve per cambiare la pagina visibile).
     * @param action L'azione da compiere (ad esempio tornare alla Login page o alla Mappa).
     */
    public BackButton(MainFrame frame, Runnable action) {
        super("Indietro");
        addActionListener(e -> action.run());
    }
}
