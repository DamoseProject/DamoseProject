package gui;

import javax.swing.*;
import java.awt.*;

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
        JPanel fieldPanel = UIComponentFactory.createVerticalPanel();

        JLabel label = UIComponentFactory.createLabel(labelName, JLabel.LEFT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(field.getPreferredSize());
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
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

    protected JPanel createCenteredContentPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel contentPanel = UIComponentFactory.createVerticalPanel();
        centerPanel.add(contentPanel, new GridBagConstraints());
        return centerPanel;
    }

    protected JPanel createVerticalContentPanel() {
        return UIComponentFactory.createVerticalPanel();
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