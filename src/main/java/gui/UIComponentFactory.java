package gui;

import javax.swing.*;
import java.awt.*;

public class UIComponentFactory {

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

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

    public static JLabel createClickableLabel(String text, String underlinedText) {
        JLabel label = new JLabel(text);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    public static JPanel createHorizontalPanel(int alignment) {
        JPanel panel = new JPanel(new FlowLayout(alignment));
        return panel;
    }

    public static JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
}