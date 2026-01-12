package testing;

import gui.BackButton;
import gui.BasePage;
import gui.MainFrame;
import gui.PageType;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per {@link BasePage}.
 * Verifica le funzionalità strutturali di base ereditate da tutte le pagine del sistema,
 * assicurando che il layout principale sia coerente e che i metodi di utilità grafica
 * (come la creazione di campi di input e la gestione degli errori) funzionino correttamente.
 * * @author Studente
 */
class BasePageTest {

    private BasePage testPage;
    private MainFrame mockFrame;

    /**
     * Configura l'ambiente di test prima di ogni metodo.
     * Inizializza un {@link MainFrame} e una versione concreta (tramite classe anonima)
     * di {@link BasePage} per permettere l'ispezione dei suoi componenti protetti.
     */
    @BeforeEach
    void setUp() {
        mockFrame = new MainFrame();
        // Creiamo un'istanza concreta minima per il test
        testPage = new BasePage(mockFrame) {};
    }

    /**
     * Verifica che il pannello principale utilizzi un {@link BorderLayout}.
     * Questo è essenziale per garantire che le barre superiori e i contenuti centrali
     * siano posizionati correttamente nelle sottoclassi.
     */
    @Test
    @DisplayName("Il mainPanel deve essere inizializzato con BorderLayout")
    void testInitialPanel() {
        assertNotNull(testPage.getPanel());
        assertTrue(testPage.getPanel().getLayout() instanceof BorderLayout);
    }

    /**
     * Testa il metodo di utilità per la creazione di pannelli di input (Label + Field).
     * Verifica che i componenti siano inseriti nel numero corretto e che le
     * proprietà di allineamento siano impostate per una corretta visualizzazione verticale.
     */
    @Test
    @DisplayName("createFieldPanel deve allineare correttamente label e campo")
    void testCreateFieldPanel() {
        JTextField field = new JTextField();
        JPanel panel = testPage.createFieldPanel("Nome:", field);

        assertEquals(2, panel.getComponentCount(), "Il pannello dovrebbe avere una label e un campo");

        JLabel label = (JLabel) panel.getComponent(0);
        assertEquals("Nome:", label.getText());
        assertEquals(Component.CENTER_ALIGNMENT, label.getAlignmentX());
    }

    /**
     * Verifica la logica di feedback per l'utente.
     * Assicura che la label di errore venga attivata visivamente e popolata
     * con il messaggio corretto quando viene invocato il metodo showError.
     */
    @Test
    @DisplayName("showError deve rendere visibile la label e impostare il messaggio")
    void testShowError() {
        JLabel errorLabel = new JLabel();
        errorLabel.setVisible(false);

        testPage.showError(errorLabel, "Errore di test");

        assertTrue(errorLabel.isVisible());
        assertEquals("Errore di test", errorLabel.getText());
    }

    /**
     * Valida la simmetria del pannello superiore.
     * Il test verifica che il titolo sia centrato grazie all'uso di un componente
     * di riempimento (Strut/Filler) a destra che bilancia la dimensione del
     * pulsante "Indietro" posto a sinistra.
     */
    @Test
    @DisplayName("createTopPanelWithBackButton deve bilanciare il layout con uno Strut a destra")
    void testTopPanelBalance() {
        JPanel topPanel = testPage.createTopPanelWithBackButton("Titolo", PageType.LOGIN);
        BorderLayout layout = (BorderLayout) topPanel.getLayout();

        assertAll("Verifica componenti TopPanel",
                () -> assertTrue(topPanel.getComponent(0) instanceof BackButton, "Deve esserci un BackButton a Ovest"),
                () -> assertTrue(topPanel.getComponent(1) instanceof JLabel, "Deve esserci un Titolo al Centro"),
                () -> assertTrue(topPanel.getComponent(2) instanceof Box.Filler, "Deve esserci uno spazio vuoto a Est per bilanciare")
        );
    }
}