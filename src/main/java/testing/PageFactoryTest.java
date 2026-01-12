package testing;

import gui.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PageFactoryTest {

    private MainFrame mockFrame;

    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
    }

    @Test
    @DisplayName("La Factory deve creare le istanze corrette per ogni PageType")
    void testCreatePage() {
        assertAll("Creazione Pagine",
                () -> assertTrue(PageFactory.createPage(PageType.LOGIN, mockFrame) instanceof LoginPage),
                () -> assertTrue(PageFactory.createPage(PageType.HELP, mockFrame) instanceof HelpPage),
                () -> assertTrue(PageFactory.createPage(PageType.MAP_GUEST, mockFrame) instanceof MapNotLogPage),
                () -> assertTrue(PageFactory.createPage(PageType.MAP_LOGGED, mockFrame) instanceof MapLogPage)
        );
    }
}