package gui;

/**
 * Questa classe serve a configurare quali pulsanti e funzioni sono attivi sulla mappa.
 * Funziona come una "scheda tecnica" che dice all'applicazione se l'utente può
 * vedere i preferiti o se deve visualizzare un messaggio di errore perché non è loggato.
 */
public class ButtonMapPageConfig {

    /** Indica se l'utente può aggiungere fermate ai preferiti (stella) */
    private final boolean favoritesEnabled;

    /** Indica se l'utente può visualizzare la lista dei suoi preferiti */
    private final boolean viewFavoritesEnabled;

    /** Indica se bisogna mostrare il tasto "Accedi o Registrati" */
    private final boolean showRegLoginButton;

    /** Il messaggio da mostrare se un ospite prova a salvare un preferito */
    private final String favoritesErrorMessage;

    /** Il messaggio da mostrare se un ospite prova a vedere la lista preferiti */
    private final String viewFavoritesErrorMessage;

    /**
     * Costruttore completo per creare una configurazione personalizzata.
     * @param favoritesEnabled Abilita o disabilita il salvataggio preferiti.
     * @param viewFavoritesEnabled Abilita o disabilita la visualizzazione lista.
     * @param showRegLoginButton Mostra o nasconde il tasto di login.
     * @param favoritesErrorMessage Errore per il salvataggio negato.
     * @param viewFavoritesErrorMessage Errore per la visualizzazione negata.
     */
    public ButtonMapPageConfig(boolean favoritesEnabled, boolean viewFavoritesEnabled, boolean showRegLoginButton,
                               String favoritesErrorMessage, String viewFavoritesErrorMessage) {
        this.favoritesEnabled = favoritesEnabled;
        this.viewFavoritesEnabled = viewFavoritesEnabled;
        this.showRegLoginButton = showRegLoginButton;
        this.favoritesErrorMessage = favoritesErrorMessage;
        this.viewFavoritesErrorMessage = viewFavoritesErrorMessage;
    }

    /**
     * Crea la configurazione per un utente che ha fatto il login.
     * Tutti i permessi sono attivi e il tasto login è nascosto.
     * @return Un oggetto config con permessi completi.
     */
    public static ButtonMapPageConfig forLoggedUser() {
        return new ButtonMapPageConfig(true, true, false,null, null);
    }

    /**
     * Crea la configurazione per un utente ospite (non loggato).
     * Le funzioni preferiti sono disattivate e vengono impostati i messaggi di errore.
     * @return Un oggetto config con restrizioni da ospite.
     */
    public static ButtonMapPageConfig forGuestUser() {
        return new ButtonMapPageConfig(false, false, true,
                Constants.LOGIN_REQUIRED_FAVORITES,
                Constants.LOGIN_REQUIRED_LIST);
    }

    /** @return true se i preferiti sono attivi */
    public boolean isFavoritesEnabled() { return favoritesEnabled; }

    /** @return true se la lista preferiti è visualizzabile */
    public boolean isViewFavoritesEnabled() { return viewFavoritesEnabled; }

    /** @return true se il tasto login deve essere visibile */
    public boolean isShowRegLoginButton() { return showRegLoginButton; }

    /** @return Il messaggio di errore per il salvataggio preferiti */
    public String getFavoritesErrorMessage() { return favoritesErrorMessage; }

    /** @return Il messaggio di errore per la visualizzazione preferiti */
    public String getViewFavoritesErrorMessage() { return viewFavoritesErrorMessage; }
}