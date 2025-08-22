package gui;

import javax.swing.*;
import java.awt.*;

public class MapLogPage extends BaseMapPage {
    public MapLogPage(MainFrame frame) {
        super(frame);
    }

    @Override
    protected JPanel createButtonsPanel(JLabel errorLabel) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        JButton addFav = new JButton("Aggiungi ai preferiti!");
        JButton checkFav = new JButton("Preferiti!");

        buttonsPanel.add(addFav);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonsPanel.add(checkFav);
        return buttonsPanel;
    }
}
