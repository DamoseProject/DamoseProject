package gui;

public class RegistrationAuth {
    private String username;
    private String email;
    private String  password;
    private String confirmPassword;

    public RegistrationAuth(RegistrationPage registrationPage) {
        this.email = registrationPage.getEmailRegistration();
        this.username = registrationPage.getUsernameRegistration();
        this.password = registrationPage.getPasswordRegistration();
        this.confirmPassword = registrationPage.getConfirmPasswordRegistration();
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
                    !password.isEmpty() && !confirmPassword.isEmpty() &&
                    password.equals(confirmPassword);
    }

    public boolean validatePasswordStrength() {

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = "!$&@#";

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

    public boolean validate() {
        return validatePresenceUsername() && validateLengthUsername() && validatePresenceEmail() && validatePasswordMatch() && validatePasswordStrength();
    }
}
