package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIComponentFactory {

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
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

    public static JButton createSymbolButton(String symbol, int fontSize) {
        JButton button = new JButton(symbol);
        button.setPreferredSize(new Dimension(30, 25));
        button.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JLabel createClickableLabel(String text, Color color, Runnable onClick) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(color);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (onClick != null) {
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
        }
        return label;
    }


    public static JLabel createLabel(String text, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVisible(false);
        return label;
    }



    public static JPanel createHorizontalPanel(int alignment) {
        return new JPanel(new FlowLayout(alignment));
    }

    public static JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
}