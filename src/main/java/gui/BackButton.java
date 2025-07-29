package gui;

import javax.swing.*;

public class BackButton extends JButton {
    public BackButton(MainFrame frame) {
        super("Indietro");
        addActionListener(e -> frame.setView(new LoginPage(frame)));
    }
}
