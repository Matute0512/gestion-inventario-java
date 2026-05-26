package io.github.torres.view.styles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class UIStyles {

    private UIStyles() {}

    /**
     * Applies consistent button styling
     */
    public static void styleButton(JButton button, Color color) {
        button.setBackground(color);

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setPreferredSize(Theme.BUTTON_SIZE);
    }

    /**
     * Creates a rounded input border
     */
    public static Border createInputBorder() {

        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public static Border createPanelBorder(String title) {

        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR),
                title);
    }

    public static JButton createButton(String text, Color color) {
        JButton button = new JButton(text);

        styleButton(button, color);

        return button;
    }

    public static JTextField createTextField() {

        JTextField field = new JTextField();

        field.setBorder(createInputBorder());

        field.setPreferredSize(Theme.INPUT_SIZE);

        return field;
    }

    public static JTextArea createTextArea() {

        JTextArea area = new JTextArea(6, 15);

        area.setLineWrap(true);

        area.setWrapStyleWord(true);

        area.setMargin(new Insets(10, 10, 10, 10));

        return area;
    }

    public static JScrollPane createScrollPane(Component component) {

        JScrollPane scrollPane = new JScrollPane(component);

        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR));

        return scrollPane;
    }
}
