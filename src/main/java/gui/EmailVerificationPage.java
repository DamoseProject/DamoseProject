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
        topPanel = createTopPanelWithBackButton("Completa la tua registrazione!", PageType.REGISTRATION);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        createVerificationCodePanel();
        errorVerificationCodeLabel = createErrorLabel();

        JButton submitButton = createStyledButton("Fine!");
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
        verificationCodeField = new JTextField(6);
        verificationCodeField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, verificationCodeField.getPreferredSize().height)
        );

        verificationCodePanel = createFieldPanel(
                "Inserisci il codice di verifica a 6 cifre che è stato inviato alla tua e-mail:",
                verificationCodeField
        );

        verificationCodePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void handleSubmit() {
        String code = getVerificationCode();

        if (code.isEmpty()) {
            showError(errorVerificationCodeLabel, Constants.MISSED_VER_CODE);
        } else if (code.length() != Constants.VERIFICATION_CODE_LENGTH) {
            showError(errorVerificationCodeLabel, Constants.WRONG_VER_CODE);
        } else {
            errorVerificationCodeLabel.setVisible(false);
            frame.setView(new MapLogPage(frame));
        }
    }

    public String getVerificationCode() {
        return verificationCodeField.getText().trim();
    }
}