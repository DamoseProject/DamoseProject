package gui;

import javax.swing.*;



public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Damose!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
