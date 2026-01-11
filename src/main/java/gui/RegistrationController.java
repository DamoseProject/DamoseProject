package gui;

import model.User;

import java.sql.SQLException;

public class RegistrationController {

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