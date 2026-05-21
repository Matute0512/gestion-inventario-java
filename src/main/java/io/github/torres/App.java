package io.github.torres;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.formdev.flatlaf.FlatLightLaf;

import io.github.torres.controller.ProductController;
import io.github.torres.dao.ProductDAO;
import io.github.torres.view.MainView;

/**
 * Main entry point for the Inventory Management System.
 */
public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                // Modern Look and Feel
                FlatLightLaf.setup();
                UIManager.put("Button.arc", 15);
                UIManager.put("Component.arc", 15);
                UIManager.put("TextComponent.arc", 10);
                UIManager.put("ProgressBar.arc", 15);

                // Global Font Settings
                Font globalFont = new Font("Segoe UI", Font.PLAIN, 15);

                Enumeration<Object> keys = UIManager.getDefaults().keys();

                while (keys.hasMoreElements()) {

                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);

                    if (value instanceof FontUIResource) {
                        UIManager.put(key, new FontUIResource(globalFont));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // MODEL
            ProductDAO productDAO = new ProductDAO();

            // VIEW
            MainView window = new MainView();

            // CONTROLLER
            new ProductController(window, productDAO);

            // SHOW WINDOW
            window.setVisible(true);
        });
    }
}
