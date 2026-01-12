package gui;

/**
 * Questa classe gestisce la sessione dell'utente corrente all'interno dell'applicazione.
 * Implementa il pattern "Singleton", assicurando che i dati dell'utente loggato
 * (ID, Username e stato del login) siano accessibili da qualsiasi parte del programma
 * senza dover passare l'oggetto tra i vari costruttori delle pagine.
 */
public class UserSession {

    /** L'unica istanza della classe disponibile nel programma */
    private static UserSession instance;

    private Integer userId;
    private String username;
    private boolean logged;

    /**
     * Costruttore privato per impedire la creazione di nuove istanze dall'esterno.
     * All'avvio, l'utente è considerato non loggato.
     */
    private UserSession() {
        logged = false;
    }

    /**
     * Restituisce l'unica istanza esistente di UserSession.
     * Se non esiste ancora, la crea.
     * * @return L'istanza condivisa della sessione.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Inizializza i dati della sessione quando un utente effettua il login con successo.
     * * @param userId L'ID univoco dell'utente recuperato dal database.
     * @param username Lo username dell'utente.
     */
    public void login(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.logged = true;
    }

    /**
     * Resetta i dati della sessione, riportando lo stato a "non loggato".
     * Utilizzato durante la fase di logout.
     */
    public void logout() {
        userId = null;
        username = null;
        logged = false;
    }

    /**
     * Verifica se un utente è attualmente autenticato nel sistema.
     * @return true se l'utente è loggato, false se è un ospite.
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * @return L'ID dell'utente loggato, o null se non c'è una sessione attiva.
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @return Lo username dell'utente loggato.
     */
    public String getUsername() {
        return username;
    }
}

