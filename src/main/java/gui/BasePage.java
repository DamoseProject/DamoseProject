package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Questa è la classe base (astratta) per tutte le pagine dell'applicazione.
 * Serve a non dover riscrivere ogni volta le funzioni comuni, come la creazione
 * di pannelli centrati, la gestione del tasto "Indietro" o la visualizzazione degli errori.
 * * Implementa {@link GeneralPanel} per assicurarsi che ogni pagina restituisca un JPanel.
 */
public abstract class BasePage implements GeneralPanel {

    /** Il frame principale dell'app, serve per cambiare pagina (navigazione) */
    protected final MainFrame frame;

    /** Il pannello principale della pagina che contiene tutti gli altri elementi */
    protected final JPanel mainPanel;

    /**
     * Costruttore: inizializza il riferimento al frame e crea il pannello principale.
     * * @param frame La finestra principale (MainFrame) dove la pagina verrà visualizzata.
     */
    protected BasePage(MainFrame frame) {
        this.frame = frame;
        this.mainPanel = new JPanel(new BorderLayout());
    }

    /**
     * Permette di cambiare il modo in cui sono disposti gli elementi nella pagina.
     * * @param layout Il LayoutManager da applicare (es. FlowLayout, GridLayout).
     */
    public void setLayout(LayoutManager layout) {
        mainPanel.setLayout(layout);
    }

    /**
     * Crea un piccolo pannello che contiene una scritta (Label) e un campo di testo.
     * È utile nei moduli (come Login o Registrazione) per tenere ordinati i campi.
     * * @param labelName Il testo da scrivere sopra al campo (es. "Username:").
     * @param field Il componente di input (es. un JTextField).
     * @return Un JPanel organizzato verticalmente con etichetta e campo.
     */
    public JPanel createFieldPanel(String labelName, JComponent field) {
        JPanel fieldPanel = UIComponentFactory.createVerticalPanel();

        JLabel label = UIComponentFactory.createLabel(labelName, JLabel.LEFT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(field.getPreferredSize());
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
    }

    /**
     * Crea una barra superiore con un titolo al centro e un pulsante "Indietro" a sinistra.
     * * @param centerText Il titolo della pagina da mostrare in alto.
     * @param backPageType Il tipo di pagina a cui tornare quando si preme "Indietro".
     * @return Il pannello superiore pronto per essere aggiunto a BorderLayout.NORTH.
     */
    public JPanel createTopPanelWithBackButton(String centerText, PageType backPageType) {
        JPanel topPanel = new JPanel(new BorderLayout());
        BackButton backButton = new BackButton(frame, () -> frame.setView(PageFactory.createPage(backPageType, frame)));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel centerLabel = new JLabel(centerText, JLabel.CENTER);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(centerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);

        return topPanel;
    }

    /**
     * Crea un pannello che centra tutto il suo contenuto sia verticalmente che orizzontalmente.
     * Molto utile per le schermate di login.
     * * @return Un JPanel configurato con GridBagLayout per centrare il contenuto.
     */
    protected JPanel createCenteredContentPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel contentPanel = UIComponentFactory.createVerticalPanel();
        centerPanel.add(contentPanel, new GridBagConstraints());
        return centerPanel;
    }

    protected JPanel createVerticalContentPanel() {
        return UIComponentFactory.createVerticalPanel();
    }

    /**
     * Fa apparire un messaggio di errore rosso su una etichetta specifica.
     * * @param errorLabel La JLabel dove scrivere l'errore.
     * @param message Il testo dell'errore (preso solitamente da Constants).
     */
    public void showError(JLabel errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Metodo obbligatorio dell'interfaccia GeneralPanel.
     * * @return Il pannello principale completo della pagina.
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}