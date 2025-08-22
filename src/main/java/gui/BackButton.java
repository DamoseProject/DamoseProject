package gui;

import javax.swing.*;

public class BackButton extends JButton {
    public BackButton(MainFrame frame, Runnable action) {
        super("Indietro");
        addActionListener(e -> action.run());
    }
}
