package gui;

public class ErrorMessages {
    public static final String USERNAME_REQUIRED = "Inserire uno username!";
    public static final String USERNAME_TOO_LONG = "Lo username deve contenere al massimo 12 caratteri!";
    public static final String EMAIL_REQUIRED = "Inserire una email per completare la registrazione!";
    public static final String PASSWORD_MISMATCH = "Le password non coincidono!";
    public static final String PASSWORD_WEAK = "La password deve contenere almeno: una lettera maiuscola, un numero e un carattere speciale tra: !,$,&,@,#.";
    public static final String PASSWORD_REQUIRED = "Inserire la password e la conferma password!";
    public static final String USERNAME_TAKEN = "Username già in uso!";
    public static final String USERNAME_OR_PSW_WRONG = "Username o password errata!";
    public static final String REGISTRATION_ERROR = "Errore durante la registrazione.";
    public static final String LOGIN_REQUIRED_FAVORITES = "Effettuare il login per aggiungere la ricerca ai Preferiti!";
    public static final String LOGIN_REQUIRED_LIST = "Effettuare il login per avere accesso alla lista dei Preferiti!";
    public static final String MISSED_RESEARCH = "Inserire una ricerca da effettuare!";
    public static final String MISSED_VER_CODE = "Inserire il codice di verifica a 6 cifre";
    public static final String WRONG_VER_CODE = "Il codice inserito è errato!";
    public static final String CONNECTION_ERROR_DATABASE = "Errore di connessione al database.";


    public static final int MAX_USERNAME_LENGTH = 12;
    public static final String SPECIAL_CHARS = "!$&@#";
    public static final int VERIFICATION_CODE_LENGTH = 6;

    private ErrorMessages() {} // Impedisce istanziazione
}

