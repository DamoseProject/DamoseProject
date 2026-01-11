package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class BasePage implements GeneralPanel {
    protected final MainFrame frame;
    protected final JPanel mainPanel;

    protected BasePage(MainFrame frame) {
        this.frame = frame;
        this.mainPanel = new JPanel(new BorderLayout());
    }

    public void setLayout(LayoutManager layout) {
        mainPanel.setLayout(layout);
    }

    public JPanel createFieldPanel(String labelName, JComponent field) {
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

    public JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVisible(false);
        return label;
    }

    protected JPanel createTopPanelWithBackButton(String centerText, PageType backPageType) {
        JPanel topPanel = new JPanel(new BorderLayout());
        BackButton backButton = new BackButton(frame, () -> frame.setView(PageFactory.createPage(backPageType, frame)));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel centerLabel = new JLabel(centerText, JLabel.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(centerLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);
        return topPanel;
    }

    protected JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    protected JLabel createClickableLabel(String text, Color color, Runnable onClick) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(color);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

        return label;
    }

    protected JPanel createCenteredContentPanel() {
        JPanel centerPanel = new JPanel();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.add(contentPanel, new GridBagConstraints());
        return centerPanel;
    }

    protected void showError(JLabel errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}