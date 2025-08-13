package gui;

import javax.swing.*;
import java.awt.*;

public class HelpPage implements GeneralPanel {
    private JPanel helpPanel;
    private JPanel topPanel;
    private JPanel centerPanel;

    private JLabel helpLabel;
    private MainFrame frame;

    public HelpPage(MainFrame frame) {
        this.frame = frame;
        helpPanel = new JPanel(new BorderLayout());

        createTopPanel();
        createCenterPanel();

        helpPanel.add(topPanel, BorderLayout.NORTH);
        helpPanel.add(centerPanel, BorderLayout.CENTER);
    }

    // ------------------ CREAZIONE PANNELLI ------------------

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());

        BackButton backButton = new BackButton(frame);
        backButton.addActionListener(e -> frame.setView(new LoginPage(frame)));

        topPanel.add(backButton, BorderLayout.WEST);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        helpLabel = new JLabel(getHelpText(), JLabel.CENTER);
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(helpLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    private String getHelpText() {
        return "<html><div style='text-align: center;'>"
                + "Perché è utile effettuare l'accesso o la registrazione alla piattaforma?<br>"
                + "Avendo un account collegato si ha la possibilità di aggiungere alla lista Preferiti "
                + "una linea o una fermata che ci interessa particolarmente."
                + "</div></html>";
    }

    // ------------------ INTERFACCIA ------------------

    @Override
    public JPanel getPanel() {
        return helpPanel;
    }
}