package gui;

import javax.swing.*;
import java.awt.*;



public class EmailVerificationPage implements GeneralPanel {
    private final JPanel emailVerificationPanel;
    private final JPanel topPanel;
    private final JPanel centerPanel;
    private final JPanel verificationCodePanel;

    private JTextField verificationCodeField;

    private JButton submitButton;
    private BackButton backButton;

    private JLabel completeRegistrationLabel;
    private JLabel verificationCodeLabel;
    private JLabel errorVerificationCodeLabel;



    public EmailVerificationPage(MainFrame frame) {
        emailVerificationPanel = new JPanel();
        emailVerificationPanel.setLayout(new BorderLayout());

        //Top panel
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        backButton = new BackButton(frame);
        completeRegistrationLabel = new JLabel("Completa la tua registrazione!", JLabel.CENTER);
        topPanel.add(backButton,  BorderLayout.WEST);
        topPanel.add(completeRegistrationLabel, BorderLayout.CENTER);

        //Aggiungo un pannello vuoto a destra per centrare la scritta Registrati
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);



        //Center panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        verificationCodeLabel = new JLabel("Inserisci il codice di verifica a 6 cifre che è stato inviato alla tua e-mail!");
        verificationCodeField = new JTextField(6);
        verificationCodePanel = new JPanel();
        verificationCodePanel.setLayout(new BoxLayout(verificationCodePanel, BoxLayout.Y_AXIS));
        verificationCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verificationCodeField.setMaximumSize(verificationCodeField.getPreferredSize());
        verificationCodePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verificationCodePanel.add(verificationCodeLabel);
        verificationCodePanel.add(verificationCodeField);


        //Error label
        errorVerificationCodeLabel = new JLabel("");
        errorVerificationCodeLabel.setForeground(Color.RED);
        errorVerificationCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorVerificationCodeLabel.setVisible(false);

        //Submit button
        submitButton = new JButton("Fine!");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            if(verificationCodeField.getText().isEmpty()) {
                errorVerificationCodeLabel.setText("Inserire il codice di verifica a 6 cifre");
                errorVerificationCodeLabel.setVisible(true);
            } else if(verificationCodeField.getText().length() != 6) {
                errorVerificationCodeLabel.setText("Il codice inserito è errato!");
                errorVerificationCodeLabel.setVisible(true);
            } else {
                errorVerificationCodeLabel.setVisible(false);
                frame.setView(new LoginPage(frame));
            }
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(verificationCodePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(submitButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(errorVerificationCodeLabel);
        centerPanel.add(Box.createVerticalGlue());




        emailVerificationPanel.add(topPanel, BorderLayout.NORTH);
        emailVerificationPanel.add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public JPanel getPanel() {
        return emailVerificationPanel;
    }

    public String getVerificationCode() {
        return verificationCodeField.getText();
    }
}
