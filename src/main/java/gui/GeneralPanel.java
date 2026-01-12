package gui;

import javax.swing.*;

/**
 * Questa interfaccia rappresenta il "contratto" base per tutti i componenti grafici dell'app.
 * Serve a garantire che ogni classe che crea una schermata (come la Login o la Mappa)
 * sia in grado di fornire il suo pannello principale al MainFrame.
 */
public interface GeneralPanel {

    /**
     * Restituisce il pannello Swing (JPanel) che contiene tutti gli elementi grafici della pagina.
     * Questo metodo permette al MainFrame di visualizzare correttamente il contenuto
     * di ogni diversa schermata.
     * * @return Il pannello principale della classe che implementa questa interfaccia.
     */
    JPanel getPanel();
}
