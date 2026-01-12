package testing;

import gui.Constants;
import gui.RegistrationController;
import gui.RegistrationResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {

    private RegistrationController controller;

    @BeforeEach
    void setUp() {
        controller = new RegistrationController();
    }

    @Test
    @DisplayName("Il controller deve bloccare username troppo lunghi")
    void testUsernameLengthValidation() {
        RegistrationResult result = controller.handleRegistration(
                "unUsernameTroppoLungoOltreDodici",
                "test@gmail.com",
                "Pass123!",
                "Pass123!"
        );

        assertFalse(result.isSuccess());
        assertEquals(Constants.USERNAME_TOO_LONG, result.getErrorMessage());
    }

    @Test
    @DisplayName("Il controller deve bloccare password che non coincidono")
    void testPasswordMatchValidation() {
        RegistrationResult result = controller.handleRegistration(
                "user", "test@gmail.com", "Pass123!", "Diversa123!"
        );

        assertFalse(result.isSuccess());
        assertEquals(Constants.PASSWORD_MISMATCH, result.getErrorMessage());
    }
}