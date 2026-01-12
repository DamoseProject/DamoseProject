package gui;

import model.Database;

import javax.swing.*;

/**
 * Questa classe gestisce la connessione con il database SQLite dell'applicazione.
 * Si occupa di inizializzare il database, controllare se siamo connessi e mostrare
 * avvisi grafici all'utente se qualcosa non funziona.
 */
public class DatabaseConnection {

    /** L'unica istanza di questa classe (Singleton) */
    private static DatabaseConnection instance;

    /** Il database vero e proprio */
    private final Database database;

    /** Flag che dice se siamo connessi o meno */
    private boolean connected;

    /**
     * Costruttore privato: crea l'oggetto del database ma non si connette ancora.
     */
    private DatabaseConnection() {
        this.database = new Database();
        this.connected = false;
    }

    /**
     * Metodo per ottenere l'unica istanza disponibile di DatabaseConnection.
     * @return L'istanza condivisa da tutta l'app.
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Prova ad aprire la connessione fisica con il file del database.
     * @return true se la connessione riesce, false se ci sono errori (es. file mancante).
     */
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


    /**
     * Fornisce l'oggetto database per fare le query.
     * @return L'oggetto Database se connesso, altrimenti null.
     */
    public Database getDatabase() {
        return connected ? database : null;
    }

    /**
     * Verifica lo stato attuale della connessione.
     * @return true se siamo connessi al DB.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Mostra una finestra di errore se il database non viene trovato all'avvio.
     * Permette all'utente di cliccare su "Riprova" o "Esci".
     * @param parent La finestra principale su cui far apparire l'errore.
     * @return true se l'utente vuole riprovare, false se vuole uscire.
     */
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

    /**
     * Gestisce la sequenza di aggiornamento dei dati GTFS all'avvio.
     * Mostra una finestra di attesa che blocca il login finché il download non è finito.
     * @param updateTask Il compito da eseguire (scaricare i dati).
     * @param onLoginAction L'azione da fare dopo (di solito mostrare la pagina di Login).
     */
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