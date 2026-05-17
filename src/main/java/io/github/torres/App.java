package io.github.torres;

import javax.swing.UIManager;

import io.github.torres.controller.ProductController;
import io.github.torres.dao.ProductDAO;
import io.github.torres.view.MainView;

/**
 * Main entry point for the Inventory Management System.
 * Implements an interactive console-based UI.
 */
public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 1. Initialize the MODEL layer (Data Access)
            ProductDAO productDAO = new ProductDAO();

            // 2. Initialize the VIEW layer (GUI Framework)
            MainView window = new MainView();

            // 3. Initialize the CONTROLLER layer (The Brain)
            // Pass view and dao references so it can bridge them together
            new ProductController(window, productDAO);

            // Display the application window
            window.setVisible(true);
        });
    }
}
