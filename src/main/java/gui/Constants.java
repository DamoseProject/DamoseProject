package gui;

/**
 * Questa classe è un raccoglitore di tutti i testi e i valori fissi (costanti) dell'app.
 * Contiene i messaggi di errore, le scritte delle etichette e i limiti di lunghezza.
 * Usarla serve a rendere il codice più pulito: se devi cambiare un messaggio,
 * lo cambi qui una volta sola e si aggiorna in tutta l'applicazione.
 */
public class Constants {
    public static final String USERNAME_REQUIRED = "Inserire uno username!";
    public static final String USERNAME_TOO_LONG = "Lo username deve contenere al massimo 12 caratteri!";
    public static final String EMAIL_REQUIRED = "Inserire una email per completare la registrazione!";
    public static final String INVALID_EMAIL = "Inserire una email valida!";
    public static final String PASSWORD_MISMATCH = "Le password non coincidono!";
    public static final String PASSWORD_WEAK = "La password deve contenere almeno: una lettera maiuscola, un numero e un carattere speciale tra: !,$,&,@,#.";
    public static final String PASSWORD_REQUIRED = "Inserire la password e la conferma password!";
    public static final String USERNAME_TAKEN = "Username già in uso!";
    public static final String USERNAME_OR_PSW_WRONG = "Username o password errata!";
    public static final String REGISTRATION_ERROR = "Errore durante la registrazione.";
    public static final String LOGIN_REQUIRED_FAVORITES = "Effettuare il login per aggiungere la ricerca ai Preferiti!";
    public static final String LOGIN_REQUIRED_LIST = "Effettuare il login per avere accesso alla lista dei Preferiti!";
    public static final String MISSED_RESEARCH = "Inserire una ricerca da effettuare!";
    public static final String MISSED_RESEARCH_DASHBOARD = "Inserire una ricerca da effettuare, per accedere alla dashboard!";
    public static final String MISSED_VER_CODE = "Inserire il codice di verifica a 6 cifre";
    public static final String WRONG_VER_CODE = "Il codice inserito è errato!";
    public static final String CONNECTION_ERROR_DATABASE = "Errore di connessione al database.";
    public static final String STOP_NOT_FOUND = "Fermata non trovata nel database.";
    public static final String WAYPOINT_ERROR = "Al momento non è possibile mostrare questa ricerca sulla mappa.";
    public static final String DB_SEARCH_ERROR = "Errore nella ricerca nel database.";
    public static final String RESULTS_HEADER = "Risultati per: ";
    public static final String NO_RESULTS = "Nessun risultato trovato";
    public static final String FAVORITES_HEADER = "I tuoi Preferiti";
    public static final String NO_FAVORITES_SAVED = "Nessun preferito salvato.";
    public static final String FAVORITES_RETRIEVAL_ERROR = "Errore nel recupero dei preferiti.";
    public static final String NO_BUS_ARRIVING = "Nessun bus in arrivo";
    public static final String DATA_RETRIEVAL_ERROR = "Errore nel recupero dati";
    public static final String USER_NOT_FOUND = "Utente non trovato";
    public static final String USER_RETRIEVAL_ERROR = "Errore nel recupero dell'utente.";
    public static final String FAV_ADDED = "Aggiunto ai preferiti: ";
    public static final String FAV_REMOVED = "Rimosso dai preferiti: ";
    public static final String FAV_UPDATE_ERROR = "Errore nell'aggiornamento dei preferiti.";


    public static final int MAX_USERNAME_LENGTH = 12;
    public static final String SPECIAL_CHARS = "!$&@#";
    public static final int VERIFICATION_CODE_LENGTH = 6;

    /**
     * Il costruttore è privato perché questa classe non deve essere mai creata con "new".
     * Si usano direttamente i suoi testi scrivendo "Constants.NOME_COSTANTE".
     */
    private Constants() {} // Impedisce istanziazione
}