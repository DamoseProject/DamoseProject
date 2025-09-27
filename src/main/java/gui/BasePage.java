package gui;

import javax.swing.*;
import java.awt.*;

public abstract class BasePage implements GeneralPanel{
    protected final MainFrame frame;
    protected final JPanel mainPanel;

    protected BasePage(MainFrame frame) {
        this.frame = frame;
        this.mainPanel = new JPanel(new BorderLayout());
    }

    /**
     * Metodo se le sotto-classi non volessero utilizzare BorderLayout in mainPanel
     */
    protected void setLayout(LayoutManager layout) {
        mainPanel.setLayout(layout);
    }

    /**
     * Crea un pannello con etichetta sopra e campo centrato.
     */
    protected JPanel createFieldPanel(String labelName, JComponent field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelName);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(field.getPreferredSize());
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
    }

    /**
     * Crea un'etichetta di errore rossa e invisibile di default.
     */
    protected JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVisible(false);
        return label;
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}

