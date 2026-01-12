package gui;

/**
 * Questa classe rappresenta l'esito di un tentativo di registrazione.
 * Invece di restituire solo un valore vero o falso, questo oggetto permette di
 * trasportare anche il messaggio di errore specifico in caso di fallimento,
 * rendendo più semplice la comunicazione tra la logica di controllo e l'interfaccia grafica.
 */
public class RegistrationResult {

    /** Indica se l'operazione è andata a buon fine */
    private final boolean success;

    /** Contiene il messaggio di errore se success è false, altrimenti è null */
    private final String errorMessage;

    /**
     * Costruttore privato. La creazione dell'oggetto deve avvenire tramite
     * i metodi statici di fabbrica (success o failure).
     * * @param success Stato del risultato.
     * @param errorMessage Messaggio associato in caso di errore.
     */
    private RegistrationResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    /**
     * Crea un oggetto che rappresenta una registrazione avvenuta con successo.
     * * @return Un'istanza di RegistrationResult con success impostato a true.
     */
    public static RegistrationResult success() {

        return new RegistrationResult(true, null);
    }

    /**
     * Crea un oggetto che rappresenta un fallimento della registrazione.
     * * @param errorMessage Il messaggio di errore visualizzabile dall'utente.
     * @return Un'istanza di RegistrationResult con success impostato a false e il relativo messaggio.
     */
    public static RegistrationResult failure(String errorMessage) {
        return new RegistrationResult(false, errorMessage);
    }

    /**
     * Verifica se la registrazione è andata a buon fine.
     * @return true se l'operazione ha avuto successo.
     */
    public boolean isSuccess() { return success; }

    /**
     * Recupera il messaggio di errore associato al fallimento.
     * @return La stringa del messaggio di errore.
     */
    public String getErrorMessage() { return errorMessage; }
}