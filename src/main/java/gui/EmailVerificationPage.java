package gui;

import javax.swing.*;
import java.awt.*;


public class EmailVerificationPage extends BasePage {
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel verificationCodePanel;
    private JTextField verificationCodeField;
    private JLabel errorVerificationCodeLabel;

    public EmailVerificationPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createCenterPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        BackButton backButton = new BackButton(frame, () -> frame.setView(new RegistrationPage(frame)));
        JLabel completeRegistrationLabel = new JLabel("Completa la tua registrazione!", JLabel.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(completeRegistrationLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        createVerificationCodePanel();
        errorVerificationCodeLabel = createErrorLabel();

        JButton submitButton = new JButton("Fine!");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> handleSubmit());

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(verificationCodePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(submitButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(errorVerificationCodeLabel);
        centerPanel.add(Box.createVerticalGlue());
    }

    private void createVerificationCodePanel() {
        verificationCodePanel = new JPanel();
        verificationCodePanel.setLayout(new BoxLayout(verificationCodePanel, BoxLayout.Y_AXIS));

        JLabel verificationCodeLabel = new JLabel(
                "Inserisci il codice di verifica a 6 cifre che è stato inviato alla tua e-mail!",
                JLabel.CENTER
        );
        verificationCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        verificationCodeField = new JTextField(6);
        verificationCodeField.setMaximumSize(verificationCodeField.getPreferredSize());
        verificationCodeField.setAlignmentX(Component.CENTER_ALIGNMENT);

        verificationCodePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verificationCodePanel.add(verificationCodeLabel);
        verificationCodePanel.add(Box.createVerticalStrut(10));
        verificationCodePanel.add(verificationCodeField);
    }

    private void handleSubmit() {
        String code = getVerificationCode();

        if (code.isEmpty()) {
            showError("Inserire il codice di verifica a 6 cifre");
        } else if (code.length() != 6) {
            showError("Il codice inserito è errato!");
        } else {
            errorVerificationCodeLabel.setVisible(false);
            frame.setView(new MapLogPage(frame));
        }
    }

    private void showError(String message) {
        errorVerificationCodeLabel.setText(message);
        errorVerificationCodeLabel.setVisible(true);
    }

    public String getVerificationCode() { return verificationCodeField.getText().trim(); }
}
