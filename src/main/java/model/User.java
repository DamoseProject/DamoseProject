package model;

import java.util.List;

/**
 * Questa classe rappresenta un utente registrato nel sistema.
 * Gestisce le informazioni personali (nome, cognome, email), le credenziali di accesso
 * (username, password) e le liste personalizzate di fermate e percorsi preferiti.
 */
public class User {


    private int id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;

    private List<Stop> favoriteStops;
    private List<Route> favoriteRoutes;

    /**
     * Costruttore per la creazione di un nuovo utente (senza ID).
     * Utilizzato solitamente durante la fase di registrazione prima del salvataggio nel database.
     * * @param name Nome dell'utente.
     * @param surname Cognome dell'utente.
     * @param username Nome utente per il login.
     * @param email Indirizzo email dell'utente.
     * @param password Password dell'account.
     */
    public User(String name, String surname, String username, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
    }


    /**
     * Costruttore per il caricamento di un utente esistente (con ID).
     * Utilizzato quando i dati vengono recuperati dal database SQLite.
     * * @param id Identificativo univoco generato dal database.
     * @param name Nome dell'utente.
     * @param surname Cognome dell'utente.
     * @param username Nome utente.
     * @param email Indirizzo email.
     * @param password Password dell'account.
     */
    public User(int id, String name, String surname, String username, String email, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /** @return L'identificativo univoco dell'utente. */
    public int getId() {
        return id;
    }


    /** @return Il nome dell'utente. */
    public String getName() {
        return name;
    }

    /** @param name Imposta il nome dell'utente. */
    public void setName(String name) {
        this.name = name;
    }

    /** @return Il cognome dell'utente. */
    public String getSurname() {
        return surname;
    }

    /** @param surname Imposta il cognome dell'utente. */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /** @return Lo username dell'utente. */
    public String getUsername() {
        return username;
    }

    /** @param username Imposta lo username dell'utente. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return L'indirizzo email dell'utente. */
    public String getEmail() {
        return email;
    }

    /** @param email Imposta l'email dell'utente. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return La password dell'utente. */
    public String getPassword() {
        return password;
    }

    /** @param password Imposta la password dell'utente. */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Aggiunge una fermata alla lista dei preferiti dell'utente.
     * @param stop La fermata da aggiungere.
     */
    void addFavoriteStop(Stop stop) {
        favoriteStops.add(stop);

    }

    /**
     * Rimuove una fermata dalla lista dei preferiti dell'utente.
     * @param stop La fermata da rimuovere.
     */
    void removeFavoriteStop(Stop stop) {
        favoriteStops.remove(stop);
    }

    /**
     * Restituisce la lista delle fermate preferite.
     * @return Una {@link List} di oggetti {@link Stop}.
     */
    List<Stop> getFavoriteStops() {
        return favoriteStops;
    }


}
