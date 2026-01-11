package gui;

public class RegistrationAuth {
    private final String username;
    private final String email;
    private final String password;
    private final String confirmPassword;

    public RegistrationAuth(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public boolean validatePresenceUsername() {

        return username != null && !username.isEmpty();
    }

    public boolean validateLengthUsername() {

        return username.length() <= Constants.MAX_USERNAME_LENGTH;
    }

    public boolean validatePresenceEmail() {

        return email != null && !email.isEmpty();
    }

    public boolean validateEmail() {
        boolean hasValidSuffix = email.endsWith("@gmail.com") ||
                email.endsWith("@tiscali.it") ||
                email.endsWith("@yahoo.com");
        return hasValidSuffix && email.indexOf('@') > 0;
    }

    public boolean validatePresencePassword() {
        return password != null && !password.isEmpty();
    }

    public boolean validatePresenceConfirmPassword() {
        return confirmPassword != null && !confirmPassword.isEmpty();
    }

    public boolean validatePasswordMatch() {
            return validatePresencePassword() && validatePresenceConfirmPassword() && password.equals(confirmPassword);
    }

    public boolean validatePasswordStrength() {

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = Constants.SPECIAL_CHARS;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

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


}
