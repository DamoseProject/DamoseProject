package gui;

/**
 * Questa classe rappresenta la pagina della mappa per l'utente loggato.
 * Estende {@link BaseMapPage}, quindi eredita tutta la struttura con mappa e ricerca,
 * ma configura i pulsanti in modo che tutte le funzioni (come i Preferiti)
 * siano attive e utilizzabili.
 */
public class MapLogPage extends BaseMapPage {

    /**
     * Costruttore della pagina. Inizializza la mappa chiamando il costruttore
     * della classe madre (BaseMapPage).
     * * @param frame Il frame principale dell'applicazione.
     */
    public MapLogPage(MainFrame frame) {
        super(frame);
    }

    /**
     * Questo metodo decide quali permessi ha l'utente in questa pagina.
     * Essendo la pagina per utenti loggati, restituisce la configurazione
     * che abilita i preferiti e nasconde il tasto di login/registrazione.
     * * @return La configurazione dei pulsanti specifica per l'utente loggato.
     */
    @Override
    public ButtonMapPageConfig getButtonConfig() {
        return ButtonMapPageConfig.forLoggedUser();
    }

}
