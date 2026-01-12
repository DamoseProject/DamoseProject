package testing;

import gui.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link PageFactory}.
 * Verifica che il meccanismo di istanziazione centralizzata delle pagine funzioni
 * correttamente per ogni tipo di schermata previsto dal sistema.
 * * <p>Questo test garantisce che la Factory restituisca sempre l'implementazione
 * concreta corretta associata alla costante {@link PageType} fornita,
 * prevenendo errori di navigazione o caricamenti di viste errate.</p>
 */
class PageFactoryTest {

    private MainFrame mockFrame;

    /**
     * Inizializza l'ambiente di test creando un frame principale fittizio
     * (mock) necessario per il costruttore di tutte le pagine prodotte dalla factory.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
    }

    /**
     * Valida la corretta generazione delle istanze per ogni tipologia di pagina.
     * Utilizza un'asserzione di gruppo per mappare i tipi enumerati alle relative
     * classi concrete (Login, Help, Map Guest, Map Logged).
     * * <p>Viene verificata l'istanza tramite l'operatore {@code instanceof} per
     * confermare l'integrità della gerarchia delle classi.</p>
     */
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