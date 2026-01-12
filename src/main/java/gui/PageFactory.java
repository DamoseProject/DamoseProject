package gui;

/**
 * Questa classe implementa il pattern "Factory" per la creazione delle pagine.
 * Invece di istanziare le pagine manualmente in giro per il codice, usiamo questa
 * classe centralizzata che, in base al {@link PageType} richiesto, restituisce
 * l'oggetto della pagina corretta.
 */
public class PageFactory {

    /**
     * Crea e restituisce una nuova pagina dell'applicazione.
     * Utilizza un'istruzione switch per decidere quale classe istanziare
     * in base al tipo di pagina richiesto.
     * * @param pageType Il tipo di pagina da creare (es. LOGIN, HELP, MAP_LOGGED).
     * @param frame Il riferimento al MainFrame, necessario per ogni pagina.
     * @return Un oggetto che implementa l'interfaccia {@link GeneralPanel}.
     */
    public static GeneralPanel createPage(PageType pageType, MainFrame frame) {
        return switch (pageType) {
            case LOGIN -> new LoginPage(frame);
            case REGISTRATION -> new RegistrationPage(frame);
            case HELP -> new HelpPage(frame);
            case EMAIL_VERIFICATION -> new EmailVerificationPage(frame);
            case MAP_LOGGED -> new MapLogPage(frame);
            case MAP_GUEST -> new MapNotLogPage(frame);
        };
    }
}
