package gui;

import javax.swing.*;
import java.awt.*;

public class MapNotLogPage extends BaseMapPage {
    public MapNotLogPage(MainFrame frame) {
        super(frame);
    }

    @Override
    protected JPanel createButtonsPanel(JLabel errorLabel) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        JButton addFav = new JButton("Aggiungi ai preferiti!");
        addFav.addActionListener(e -> {
            errorLabel.setText("Effettuare il login per aggiungere la ricerca ai Preferiti!");
            errorLabel.setVisible(true);
        });

        JButton checkFav = new JButton("Preferiti!");
        checkFav.addActionListener(e -> {
            errorLabel.setText("Effettuare il login per avere accesso alla lista dei Preferiti!");
            errorLabel.setVisible(true);
        });

        buttonsPanel.add(addFav);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonsPanel.add(checkFav);
        return buttonsPanel;
    }
}




