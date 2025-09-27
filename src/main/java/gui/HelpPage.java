package gui;

import javax.swing.*;
import java.awt.*;

public class HelpPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;

    public HelpPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        BackButton backButton = new BackButton(frame, () -> frame.setView(PageFactory.createPage(PageType.LOGIN, frame)));
        topPanel.add(backButton, BorderLayout.WEST);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel helpLabel = new JLabel(getHelpText(), JLabel.CENTER);
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(helpLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    private String getHelpText() {
        return "<html><div style='text-align: center;'>"
                + "Perché è utile effettuare l'accesso o la registrazione alla piattaforma?<br>"
                + "<br>"
                + "Avendo un account collegato si ha la possibilità di aggiungere alla lista Preferiti "
                + "una linea o una fermata che ci interessa particolarmente.<br>"
                + "<br>"
                + "<br>"
                + "Come zoomare sulla mappa?<br>"
                + "<br>"
                + "Per effettuare lo zoom sulla mappa si possono utilizzare i tasti + e - presenti sulla propria tastiera."
                + "</div></html>";
    }
}