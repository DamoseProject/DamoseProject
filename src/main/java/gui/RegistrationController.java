package gui;

import model.Database;
import model.User;
import java.sql.SQLException;

public class RegistrationController {

    public RegistrationResult handleRegistration(String username, String email, String password, String confirmPassword) {

        RegistrationAuth auth = new RegistrationAuth(username, email, password, confirmPassword);

        if (!auth.validatePresenceUsername()) {
            return RegistrationResult.failure(ErrorMessages.USERNAME_REQUIRED);
        }
        if (!auth.validateLengthUsername()) {
            return RegistrationResult.failure(ErrorMessages.USERNAME_TOO_LONG);
        }
        if (!auth.validatePresenceEmail()) {
            return RegistrationResult.failure(ErrorMessages.EMAIL_REQUIRED);
        }
        if (!auth.validatePresencePassword() || !auth.validatePresenceConfirmPassword()) {
            return RegistrationResult.failure(ErrorMessages.PASSWORD_REQUIRED);
        }
        if (!auth.validatePasswordMatch()) {
            return RegistrationResult.failure(ErrorMessages.PASSWORD_MISMATCH);
        }
        if (!auth.validatePasswordStrength()) {
            return RegistrationResult.failure(ErrorMessages.PASSWORD_WEAK);
        }

        // Controllo username esistente e registrazione
        try {
            Database db = new Database();
            db.connect();
            UserAuth userAuth = new UserAuth(db);

            if (userAuth.isUsernameTaken(username)) {
                return RegistrationResult.failure(ErrorMessages.USERNAME_TAKEN);
            }


            return RegistrationResult.success();

        } catch (SQLException ex) {
            return RegistrationResult.failure(ErrorMessages.REGISTRATION_ERROR);
        }
    }
}
