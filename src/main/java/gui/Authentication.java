package gui;

import java.util.Arrays;

public class Authentication {
    private String username;
    private String email;
    private char[]  password;
    private char[] confirmPassword;

    public Authentication(RegistrationPage registrationPage) {
        this.email = registrationPage.getEmailField();
        this.username = registrationPage.getUsernameField();
        this.password = registrationPage.getPasswordField();
        this.confirmPassword = registrationPage.getConfirmPasswordField();
    }

    public boolean validatePresenceUsername() {
        return username != null && !username.isEmpty();
    }

    public boolean validateLengthUsername() {
        return username.length() <= 12;
    }

    public boolean validatePresenceEmail() {
        return email != null && !email.isEmpty();
    }

    public boolean validatePasswordMatch() {
            return password != null && confirmPassword != null &&
                    password.length > 0 && confirmPassword.length > 0 &&
                    Arrays.equals(password, confirmPassword);
    }

    public boolean validatePasswordStrength() {

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = "!$&@#";

        for (int i = 0; i < password.length; i++) {
            char c = password[i];

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

    public boolean validate() {
        return validatePresenceUsername() && validateLengthUsername() && validatePresenceEmail() && validatePasswordMatch() && validatePasswordStrength();
    }
}
