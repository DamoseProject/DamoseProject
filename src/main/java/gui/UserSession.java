package gui;

public class UserSession {

    private static UserSession instance;

    private Integer userId;
    private String username;
    private boolean logged;

    private UserSession() {
        logged = false;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.logged = true;
    }

    public void logout() {
        userId = null;
        username = null;
        logged = false;
    }

    public boolean isLogged() {
        return logged;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}

