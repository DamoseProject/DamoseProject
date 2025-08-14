package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Damose!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setView(new LoginPage(this));
        setVisible(true);
    }

    public void setView(GeneralPanel view) {
        setContentPane(view.getPanel());
        revalidate();  //gestione struttura e layout
        repaint();     //"refresh" grafico
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(MainFrame::new);
    }

}
