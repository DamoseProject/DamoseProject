package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Questa classe funge da fabbrica centrale (Factory) per i componenti della GUI.
 * Fornisce metodi statici per creare pulsanti, etichette e pannelli pre-configurati.
 * L'obiettivo è centralizzare lo stile grafico dell'applicazione in un unico punto,
 * evitando la duplicazione di codice per le impostazioni estetiche (font, cursori, bordi).
 */
public class UIComponentFactory {

    /**
     * Crea un pulsante standard con cursore a mano e allineamento centrale.
     * @param text Il testo da visualizzare nel pulsante.
     * @return Un oggetto JButton configurato.
     */
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    /**
     * Crea un pulsante basato su icone testuali (Emoji o simboli) con dimensioni specifiche.
     * @param icon Il simbolo da mostrare.
     * @param size La dimensione del font e del pulsante.
     * @return Un JButton senza bordi e con sfondo trasparente.
     */
    public static JButton createIconButton(String icon, int size) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(size, size - 5));
        button.setFont(new Font("SansSerif", Font.PLAIN, size - 10));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Crea il pulsante specifico con l'icona del segnaposto usato per la mappa.
     * @return Un JButton configurato per le interazioni con la mappa.
     */
    public static JButton createMapButton() {
        JButton mapButton = new JButton("📍");
        mapButton.setPreferredSize(new Dimension(30, 25));
        mapButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mapButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        mapButton.setContentAreaFilled(false);
        mapButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mapButton.setFocusPainted(false);
        return mapButton;
    }

    /**
     * Crea un pulsante freccia utilizzato per espandere le righe dei risultati.
     * @return Un JButton minimalista per le funzioni di espansione.
     */
    public static JButton createArrowButton() {
        JButton arrowButton = new JButton("<html>▶</html>");
        arrowButton.setPreferredSize(new Dimension(20, 20));
        arrowButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        arrowButton.setContentAreaFilled(false);
        arrowButton.setBorderPainted(false);
        arrowButton.setFocusPainted(false);
        arrowButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return arrowButton;
    }

    /**
     * Crea un pulsante con un simbolo personalizzato e dimensione del font specifica.
     * @param symbol Il testo o simbolo da visualizzare.
     * @param fontSize Dimensione del carattere.
     * @return Un JButton trasparente con il simbolo indicato.
     */
    public static JButton createSymbolButton(String symbol, int fontSize) {
        JButton button = new JButton(symbol);
        button.setPreferredSize(new Dimension(30, 25));
        button.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Crea un'etichetta cliccabile.
     * Include effetti di sottolineatura quando il mouse entra nell'area.
     * @param text Testo dell'etichetta.
     * @param color Colore del testo.
     * @param onClick Azione da eseguire al click (Runnable).
     * @return Un JLabel interattivo.
     */
    public static JLabel createClickableLabel(String text, Color color, Runnable onClick) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(color);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (onClick != null) {
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    label.setText("<html><u>" + text + "</u></html>");
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    label.setText(text);
                }
            });
        }
        return label;
    }


    /**
     * Crea una label standard con allineamento specificato.
     * @param text Testo della label.
     * @param alignment Allineamento (es. JLabel.CENTER).
     * @return Un JLabel configurato.
     */
    public static JLabel createLabel(String text, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea un'etichetta specifica per i messaggi di errore.
     * Inizialmente invisibile, con testo di colore rosso e allineata al centro.
     * @return Un JLabel per la gestione degli errori.
     */
    public static JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVisible(false);
        return label;
    }



    /**
     * Crea un pannello con orientamento orizzontale.
     * @param alignment Allineamento dei componenti.
     * @return Un JPanel configurato.
     */
    public static JPanel createHorizontalPanel(int alignment) {
        return new JPanel(new FlowLayout(alignment));
    }

    /**
     * Crea un pannello con orientamento verticale.
     * @return Un JPanel pronto per impilare componenti verticalmente.
     */
    public static JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
}