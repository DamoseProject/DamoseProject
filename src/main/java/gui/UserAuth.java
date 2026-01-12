package gui;

import model.Database;
import model.User;

import java.sql.SQLException;

/**
 * Questa classe gestisce la logica di autenticazione degli utenti.
 * Si occupa di verificare se uno username è già presente nel sistema e di
 * validare le credenziali (username e password) durante la fase di login,
 * facendo da tramite tra la GUI e il database.
 */
public class UserAuth {

    /** Riferimento al database per le interrogazioni sugli utenti */
    private final Database db;

    /**
     * Costruttore: inizializza il gestore dell'autenticazione.
     * @param db L'istanza del database da utilizzare per i controlli.
     */
    public UserAuth(Database db) {
        this.db = db;

    }

    /**
     * Verifica se un determinato username è già stato utilizzato da un altro utente.
     * Utilizzato durante la fase di registrazione per evitare duplicati.
     * @param username Lo username da controllare.
     * @return true se lo username esiste già nel database, false altrimenti.
     * @throws SQLException In caso di errori durante la query al database.
     */
    public boolean isUsernameTaken(String username) throws SQLException {
        return db.isUserRegistered(username);
    }

    /**
     * Esegue il tentativo di accesso di un utente.
     * Recupera l'utente dal database tramite lo username e confronta la password
     * fornita con quella salvata.
     * * @param username Lo username inserito nel modulo di login.
     * @param password La password inserita nel modulo di login.
     * @return L'oggetto {@link User} se l'autenticazione ha successo, null se lo username
     * non esiste o la password è errata.
     * @throws SQLException In caso di problemi di connessione o esecuzione SQL.
     */
    public User login(String username, String password) throws SQLException {
        User user = db.getUserByUsername(username);

        if (user == null || password == null) {
            return null;
        }

        String pwd = user.getPassword();
        if (pwd != null && pwd.equals(password)) {
            return user;
        }

        return null;
    }


}