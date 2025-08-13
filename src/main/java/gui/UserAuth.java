package gui;

import model.Database;

import java.sql.SQLException;

public class UserAuth {
    private final Database db;

    public UserAuth(Database db) {
        this.db = db;

    }

    public boolean isUsernameUnique(String username) throws SQLException {
        return db.isUserRegistered(username);
    }

    public boolean isLoginValid(String username, String password) throws SQLException {
        return db.isUserRegistered(username);
    }
}
