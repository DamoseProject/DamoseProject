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

    public User login(String username, String password) throws SQLException {
        User user = db.getUserByUsername(username);

        if (user == null || password == null) {
            return null;
        }

        String pwd = user.getPassword();
        if (pwd != null && pwd.equals(password)) {
            return user;
        }

        return null;
    }


}