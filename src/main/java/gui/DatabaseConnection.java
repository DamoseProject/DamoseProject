package gui;

import model.Database;

import javax.swing.*;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Database database;
    private boolean connected;

    private DatabaseConnection() {
        this.database = new Database();
        this.connected = false;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public boolean connect() {
        if (connected) {
            return true;
        }

        try {
            database.connect();
            connected = true;
            return true;
        } catch (Exception e) {
            connected = false;
            return false;
        }
    }


    public Database getDatabase() {
        return connected ? database : null;
    }

    public boolean isConnected() {
        return connected;
    }

    public static boolean showConnectionErrorDialog(JFrame parent) {
        int choice = JOptionPane.showOptionDialog(
                parent,
                "Impossibile connettersi al database.\n" +
                        "Verifica che il file 'RomeBusDatabase.db' sia presente\n" +
                        "nella directory del progetto e riprova.",
                "Errore di Connessione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                new String[]{"Riprova", "Esci"},
                "Riprova"
        );

        return choice == JOptionPane.YES_OPTION;
    }

    public static void showUpdateAndLoginSequential(Runnable updateTask, Runnable onLoginAction) {
        JButton btnProceed = new JButton("Vai al Login");
        btnProceed.setEnabled(false);

        JLabel messageLabel = new JLabel("Caricamento dati in corso! Attendere.");

        JOptionPane pane = new JOptionPane(
                messageLabel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{btnProceed},
                btnProceed
        );

        JDialog dialog = pane.createDialog("Aggiornamento Database");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setModal(false);
        dialog.setVisible(true);
        dialog.paint(dialog.getGraphics());

        try {
            updateTask.run();
            messageLabel.setText("Aggiornamento completato!");
            btnProceed.setEnabled(true);

            btnProceed.addActionListener(e -> {
                dialog.dispose();
                onLoginAction.run();
            });
            dialog.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore critico: " + e.getMessage());
            System.exit(1);
        }
    }
}