package gui;

import model.Database;
import model.User;

import java.sql.SQLException;

public class UserAuth {
    private final Database db;

    public UserAuth(Database db) {
        this.db = db;

    }

    public boolean isUsernameTaken(String username) throws SQLException {
        return db.isUserRegistered(username);
    }

    public boolean isLoginValid(String username, String password) throws SQLException {
        User user = db.getUserByUsername(username);
        String pwd = user.getPassword();
        return db.isUserRegistered(username) && pwd.equals(password);
    }
}
