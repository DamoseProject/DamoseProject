package gui;

/**
 * Questa classe rappresenta la pagina della mappa per l'utente "Ospite" (non loggato).
 * Estende {@link BaseMapPage} per mostrare la mappa e la ricerca, ma imposta
 * delle limitazioni: l'utente potrà vedere i bus ma non potrà salvare i preferiti.
 */
public class MapNotLogPage extends BaseMapPage {

    /**
     * Costruttore della pagina per ospiti.
     * Chiama il costruttore della classe madre per preparare la grafica della mappa.
     * * @param frame Il frame principale dell'applicazione.
     */
    public MapNotLogPage(MainFrame frame) {
        super(frame);
    }

    /**
     * Definisce i permessi per l'utente non loggato.
     * In questo caso, restituisce la configurazione per "Guest", che disabilita
     * le funzioni dei preferiti e mostra il pulsante per accedere o registrarsi.
     * * @return La configurazione dei pulsanti specifica per l'utente ospite.
     */
    @Override
    public ButtonMapPageConfig getButtonConfig() {
        return ButtonMapPageConfig.forGuestUser();
    }
}




