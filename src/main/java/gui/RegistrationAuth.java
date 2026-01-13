package gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Questa classe si occupa della validazione dei dati inseriti durante la registrazione.
 * Funziona come un "filtro" che controlla se lo username, l'email, la password e la conferma della password
 * rispettano i requisiti di sicurezza e formato prima di permettere il salvataggio nel database.
 */
public class RegistrationAuth {
    private final String username;
    private final String email;
    private final String password;
    private final String confirmPassword;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Costruttore: riceve i dati inseriti dall'utente nei campi della pagina di registrazione.
     * @param username Lo username scelto.
     * @param email L'indirizzo email inserito.
     * @param password La password scelta.
     * @param confirmPassword La conferma della password.
     */
    public RegistrationAuth(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    /**
     * Controlla che il campo username non sia nullo o vuoto.
     * @return true se lo username è presente.
     */
    public boolean validatePresenceUsername() {

        return username != null && !username.isEmpty();
    }

    /**
     * Verifica che lo username non superi la lunghezza massima definita nelle costanti.
     * @return true se la lunghezza è valida.
     */
    public boolean validateLengthUsername() {

        return username.length() <= Constants.MAX_USERNAME_LENGTH;
    }

    /**
     * Controlla che il campo email non sia nullo o vuoto.
     * @return true se l'email è presente.
     */
    public boolean validatePresenceEmail() {

        return email != null && !email.isEmpty();
    }

    /**
     * Verifica che l'email abbia un formato valido.
     * @return true se l'email rispetta i criteri di formato.
     */
    public boolean validateEmail() {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return  matcher.matches();
    }

    /**
     * Controlla che il campo password non sia vuoto.
     * @return true se la password è presente.
     */
    public boolean validatePresencePassword() {
        return password != null && !password.isEmpty();
    }

    /**
     * Controlla che il campo di conferma password non sia vuoto.
     * @return true se la conferma è presente.
     */
    public boolean validatePresenceConfirmPassword() {
        return confirmPassword != null && !confirmPassword.isEmpty();
    }

    /**
     * Verifica che la password e la conferma siano identiche.
     * @return true se le due password coincidono.
     */
    public boolean validatePasswordMatch() {
        return validatePresencePassword() && validatePresenceConfirmPassword() && password.equals(confirmPassword);
    }

    /**
     * Valuta la robustezza della password.
     * Per essere valida, deve contenere almeno una maiuscola, un numero e un
     * carattere speciale tra quelli definiti in {@link Constants#SPECIAL_CHARS}.
     * @return true se la password è considerata sicura.
     */
    public boolean validatePasswordStrength() {

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = Constants.SPECIAL_CHARS;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {      //Almeno una maiuscola
                hasUppercase = true;
            } else if (Character.isDigit(c)) {   //Almeno un numero
                hasDigit = true;
            } else if (specialChars.indexOf(c) >= 0) {     //Almeno un carattere speciale
                hasSpecialChar = true;
            }
        }

        return hasUppercase && hasDigit && hasSpecialChar;
    }


}
