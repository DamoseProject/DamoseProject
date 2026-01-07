package gui;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Damose!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        if (!initializeDatabaseConnection()) {
            System.exit(0);
            return;
        }

        setView(new LoginPage(this));
        setVisible(true);
    }

    private boolean initializeDatabaseConnection() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        while (!dbConnection.connect()) {
            boolean retry = DatabaseConnection.showConnectionErrorDialog(this);
            if (!retry) {
                return false;
            }
        }
        return true;
    }

    public void setView(GeneralPanel view) {
        setContentPane(view.getPanel());
        revalidate();  // gestione struttura e layout
        repaint();     // "refresh" grafico
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}