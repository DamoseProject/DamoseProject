package gui;

import model.User;

import java.sql.SQLException;

/**
 * Questa classe coordina l'intero processo di registrazione di un nuovo utente.
 * Agisce come un controllore che:
 * 1. Riceve i dati dalla pagina di registrazione.
 * 2. Utilizza {@link RegistrationAuth} per validare i requisiti di sicurezza.
 * 3. Interroga il database per verificare che lo username non sia già esistente.
 * 4. Salva il nuovo profilo utente se tutti i controlli hanno esito positivo.
 */
public class RegistrationController {

    /**
     * Gestisce la richiesta di registrazione analizzando i dati inseriti.
     * Segue un ordine preciso di validazione: prima controlla la forma dei dati (presenza,
     * lunghezza, corrispondenza password), poi la disponibilità dello username nel DB
     * e infine esegue l'operazione di inserimento.
     * * @param username Lo username inserito dall'utente.
     * @param email L'indirizzo email inserito.
     * @param password La password scelta.
     * @param confirmPassword La conferma della password inserita.
     * @return Un oggetto {@link RegistrationResult} che contiene l'esito (successo o errore)
     * e il messaggio da mostrare all'utente.
     */
    public RegistrationResult handleRegistration(String username, String email, String password, String confirmPassword) {

        RegistrationAuth auth = new RegistrationAuth(username, email, password, confirmPassword);

        if (!auth.validatePresenceUsername()) {
            return RegistrationResult.failure(Constants.USERNAME_REQUIRED);
        }
        if (!auth.validateLengthUsername()) {
            return RegistrationResult.failure(Constants.USERNAME_TOO_LONG);
        }
        if (!auth.validatePresenceEmail()) {
            return RegistrationResult.failure(Constants.EMAIL_REQUIRED);
        }
        if (!auth.validatePresencePassword() || !auth.validatePresenceConfirmPassword()) {
            return RegistrationResult.failure(Constants.PASSWORD_REQUIRED);
        }
        if (!auth.validatePasswordMatch()) {
            return RegistrationResult.failure(Constants.PASSWORD_MISMATCH);
        }
        if (!auth.validatePasswordStrength()) {
            return RegistrationResult.failure(Constants.PASSWORD_WEAK);
        }
        if(!auth.validateEmail()){
            return RegistrationResult.failure(Constants.INVALID_EMAIL);
        }

        try {
            var db = DatabaseConnection.getInstance().getDatabase();

            if (db == null) {
                return RegistrationResult.failure(Constants.CONNECTION_ERROR_DATABASE);
            }

            UserAuth userAuth = new UserAuth(db);

            if (userAuth.isUsernameTaken(username)) {
                return RegistrationResult.failure(Constants.USERNAME_TAKEN);
            }

            User newUser = new User("", "", username, email, password);
            int result = db.addUser(newUser);

            if (result == 0) {
                return RegistrationResult.success();
            } else {
                return RegistrationResult.failure(Constants.REGISTRATION_ERROR);
            }

        } catch (SQLException ex) {
            return RegistrationResult.failure(Constants.REGISTRATION_ERROR);
        }
    }
}