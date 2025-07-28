package gui;

import java.util.Arrays;

public class Authentication {
    private char[]  password;
    private char[] confirmPassword;

    public Authentication(RegistrationPage registrationPage) {
        this.password = registrationPage.getPasswordField();
        this.confirmPassword = registrationPage.getConfirmPasswordField();
    }

    public boolean validatePasswordMatch() {
            return password != null
                    && confirmPassword != null
                    && Arrays.equals(password, confirmPassword);
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
        return validatePasswordMatch() && validatePasswordStrength();
    }
}
