package testing;

import gui.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PageFactoryTest {

    @Test
    @DisplayName("La Factory dovrebbe creare l'istanza corretta per ogni PageType")
    void testCreatePage() {
        // Usiamo null per il MainFrame per i test unitari,
        // a meno che i costruttori delle pagine non richiedano un frame non nullo.
        MainFrame dummyFrame = null;

        assertAll("Verifica creazione pagine",
                () -> assertTrue(PageFactory.createPage(PageType.MAP_LOGGED, dummyFrame) instanceof MapLogPage,
                        "Dovrebbe restituire un'istanza di MapLogPage"),

                () -> assertTrue(PageFactory.createPage(PageType.MAP_GUEST, dummyFrame) instanceof MapNotLogPage,
                        "Dovrebbe restituire un'istanza di MapNotLogPage"),

                () -> assertTrue(PageFactory.createPage(PageType.LOGIN, dummyFrame) instanceof LoginPage,
                        "Dovrebbe restituire un'istanza di LoginPage"),

                () -> assertTrue(PageFactory.createPage(PageType.REGISTRATION, dummyFrame) instanceof RegistrationPage,
                        "Dovrebbe restituire un'istanza di RegistrationPage"),

                () -> assertTrue(PageFactory.createPage(PageType.HELP, dummyFrame) instanceof HelpPage,
                        "Dovrebbe restituire un'istanza di HelpPage"),

                () -> assertTrue(PageFactory.createPage(PageType.EMAIL_VERIFICATION, dummyFrame) instanceof EmailVerificationPage,
                        "Dovrebbe restituire un'istanza di EmailVerificationPage")
        );
    }

    @Test
    @DisplayName("La Factory non dovrebbe restituire mai null")
    void testCreatePageNotNull() {
        GeneralPanel page = PageFactory.createPage(PageType.MAP_GUEST, null);
        assertNotNull(page, "La factory non deve restituire null per i tipi definiti nell'Enum");
    }
}