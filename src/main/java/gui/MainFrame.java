package gui;

import Scraper.GtfsDownloader;
import Scraper.UpdateData;

import javax.swing.*;

/**
 * Questa è la classe principale dell'applicazione (il Frame).
 * Rappresenta la finestra fisica che l'utente vede sullo schermo.
 * Si occupa di inizializzare il programma, controllare se ci sono aggiornamenti
 * per i dati dei bus e gestire il passaggio da una pagina all'altra.
 */
public class MainFrame extends JFrame {

    /**
     * Costruttore: imposta le caratteristiche della finestra (titolo, dimensioni).
     * All'avvio, prova a connettersi al database e controlla se i dati GTFS
     * devono essere aggiornati prima di mostrare la pagina di Login.
     */
    public MainFrame() {
        setTitle("Damose!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        if (!initializeDatabaseConnection()) {
            System.exit(0);
            return;
        }

        if (GtfsDownloader.checkForUpdates()) {

            DatabaseConnection.showUpdateAndLoginSequential(
                    () -> {
                        UpdateData.updateIfNew();
                    },
                    () -> {
                        setView(new LoginPage(this));
                        setVisible(true);
                    }
            );

        } else {
            setView(new LoginPage(this));
            setVisible(true);
        }


    }

    /**
     * Prova a stabilire una connessione con il database SQLite.
     * Se non ci riesce (es. file mancante), mostra una finestra di errore
     * e permette all'utente di riprovare.
     * * @return true se la connessione è avvenuta con successo.
     */
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

    /**
     * Questo è il metodo più importante per la navigazione.
     * Sostituisce il contenuto attuale della finestra con una nuova pagina.
     * * @param view La nuova pagina da visualizzare (deve implementare GeneralPanel).
     */
    public void setView(GeneralPanel view) {
        setContentPane(view.getPanel());
        revalidate();  // gestione struttura e layout
        repaint();     // "refresh" grafico
    }

    /**
     * Punto di ingresso principale del programma.
     * Avvia l'interfaccia grafica usando il thread dedicato di Swing (Event Dispatch Thread).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}