package gui;

/**
 * Questa enumerazione definisce tutti i tipi di pagine disponibili nell'applicazione.
 * Viene utilizzata dalla {@link PageFactory} e dal sistema di navigazione per identificare
 * in modo univoco quale schermata mostrare, evitando errori di battitura che
 * potrebbero capitare usando delle semplici stringhe.
 */
public enum PageType {

    /** La pagina di accesso dove l'utente inserisce le credenziali. */
    LOGIN,

    /** La pagina per la creazione di un nuovo account utente. */
    REGISTRATION,

    /** La pagina delle FAQ (Domande Frequenti) e assistenza. */
    HELP,

    /** La pagina dove inserire il codice di conferma inviato via e-mail. */
    EMAIL_VERIFICATION,

    /** La visualizzazione della mappa con tutte le funzioni attive per utenti registrati. */
    MAP_LOGGED,

    /** La visualizzazione della mappa con funzioni limitate per utenti ospiti. */
    MAP_GUEST
}
