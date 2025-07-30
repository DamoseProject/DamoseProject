package gui;

import javax.swing.*;
import java.awt.*;

public class HelpPage implements GeneralPanel{
    private JPanel helpPanel;
    private JPanel topPanel;
    private JPanel centerPanel;

    private BackButton backButton;
    private JLabel helpLabel = new JLabel("", JLabel.CENTER);

    private String helpText;

    public HelpPage(MainFrame frame){
        helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());

        //Top Panel
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        backButton = new BackButton(frame);
        backButton.addActionListener(e -> frame.setView(new LoginPage(frame)));
        topPanel.add(backButton, BorderLayout.WEST);


        //Center text
        helpText = "<html><div style='text-align: center;'>"
                + "Perché è utile effettuare l'accesso o la registrazione alla piattaforma?<br>"
                + "Avendo un account collegato si ha la possibilità di aggiungere alla lista Preferiti "
                + "una linea o una fermata che ci interessa particolarmente."
                + "</div></html>";
        helpLabel.setText(helpText);


        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));


        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(helpLabel);
        centerPanel.add(Box.createVerticalGlue());


        helpPanel.add(topPanel, BorderLayout.NORTH);
        helpPanel.add(centerPanel, BorderLayout.CENTER);

    }

    @Override
    public JPanel getPanel() {
        return helpPanel;
    }
}
