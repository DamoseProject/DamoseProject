package gui;

import model.Database;

import java.sql.SQLException;

public class LoginAuth {
    private final Database db;

    public LoginAuth(Database db) {
        this.db = db;

    }

    public boolean isLoginValid(String username, String password) throws SQLException {
        return db.isUserRegistered(username);
    }
}
