package gui;

import javax.swing.*;
import java.awt.*;

public class HelpPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;

    public HelpPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createTopPanel() {
        topPanel = createTopPanelWithBackButton("Q&A", PageType.LOGIN);
    }

    private void createCenterPanel() {
        centerPanel = UIComponentFactory.createVerticalPanel();

        JLabel helpLabel = UIComponentFactory.createLabel(getHelpText(), JLabel.CENTER);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(helpLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

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