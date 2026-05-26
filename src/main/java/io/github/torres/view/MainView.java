package io.github.torres.view;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import io.github.torres.view.panels.InventoryPanel;
import io.github.torres.view.panels.SalesPanel;
import io.github.torres.view.styles.Theme;

/**
 * Main application window.
 *
 * <p>
 * Acts as the main container of the system. Responsible only for window configuration and panel
 * organization.
 * </p>
 */
public class MainView extends JFrame {

    // Panels
    private InventoryPanel inventoryPanel;

    private SalesPanel salesPanel;

    private JTabbedPane tabbedPane;

    /**
     * Creates the main application window.
     */
    public MainView() {

        // Window Configuration
        configureWindow();

        initializeComponents();

        buildLayout();
    }

    private void configureWindow() {

        setTitle("SGE - Gestión de Inventario");

        setSize(1100, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        getContentPane().setBackground(Theme.BACKGROUND_COLOR);

        setMinimumSize(new Dimension(Theme.DIALOG_MIN_WIDTH,Theme.DIALOG_MIN_HEIGHT));
    }

    /**
     * Initializes UI components.
     */
    private void initializeComponents() {

        tabbedPane = new JTabbedPane();

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        inventoryPanel = new InventoryPanel();

        salesPanel = new SalesPanel();
    }

    /**
     * Builds the main layout.
     */
    private void buildLayout() {

        tabbedPane.addTab("Gestión Inventario", inventoryPanel);

        tabbedPane.addTab("Punto de Venta", salesPanel);

        add(tabbedPane, BorderLayout.CENTER);

    }

    // Getters
    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }

    public SalesPanel getSalesPanel() {
        return salesPanel;
    }
}
