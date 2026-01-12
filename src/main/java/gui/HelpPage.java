package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Questa classe rappresenta la pagina di aiuto (Q&A) dell'applicazione.
 * Fornisce all'utente le risposte alle domande più frequenti, come i vantaggi
 * della registrazione, come usare lo zoom sulla mappa e come aggiornare i dati dei bus.
 */
public class HelpPage extends BasePage {

    /** Pannello superiore che contiene il titolo e il tasto indietro */
    private JPanel topPanel;

    /** Pannello centrale che contiene il testo informativo */
    private JPanel centerPanel;

    /**
     * Costruttore della pagina. Inizializza la struttura grafica e carica i testi.
     * @param frame Il frame principale (MainFrame) dove verrà visualizzata la pagina.
     */
    public HelpPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Crea la barra superiore con il titolo "Q&A" e il pulsante per tornare alla Login.
     */
    private void createTopPanel() {
        topPanel = createTopPanelWithBackButton("Q&A", PageType.LOGIN);
    }

    /**
     * Crea il contenuto centrale della pagina.
     * Utilizza un'etichetta (JLabel) che supporta il formato HTML per mostrare il testo.
     */
    private void createCenterPanel() {
        centerPanel = UIComponentFactory.createVerticalPanel();

        JLabel helpLabel = UIComponentFactory.createLabel(getHelpText(), JLabel.CENTER);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(helpLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    /**
     * Restituisce il contenuto testuale della pagina di aiuto.
     * Il testo è formattato in HTML per permettere l'uso di stili come
     * il grassetto, il sottolineato e l'allineamento centrato.
     * @return Una stringa contenente il codice HTML per il testo di aiuto.
     */
    private String getHelpText() {
        return "<html><div style='text-align: center;'>"
                + "<u>Perché è utile effettuare l'accesso o la registrazione alla piattaforma?</u><br>"
                + "<br>"
                + "Avendo un account collegato si ha la possibilità di aggiungere alla lista Preferiti "
                + "una linea o una fermata che interessa particolarmente.<br>"
                + "<br>"
                + "<br>"
                + "<u>Come effettuare lo zoom sulla mappa?</u><br>"
                + "<br>"
                + "Per effettuare lo zoom sulla mappa si possono utilizzare i tasti + e - presenti sulla propria tastiera o"
                + "<br>"
                + "la rotellina del mouse."
                + "<br>"
                + "<br>"
                + "<br>"
                + "<u>Come effettuare il refresh della posizione dei bus sulla mappa e il loro tempo di arrivo?</u><br>"
                + "<br>"
                + "Per ottenere il refresh dei dati occorre eseguire nuovamente la ricerca (i dati si aggiornano ogni 30 secondi circa)."
                + "</div></html>";
    }
}