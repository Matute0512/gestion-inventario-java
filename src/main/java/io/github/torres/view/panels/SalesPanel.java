package io.github.torres.view.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import io.github.torres.view.styles.Theme;
import io.github.torres.view.styles.UIStyles;

/**
 * Point of Sale (POS) panel. Contains the product catalog search and the active shopping cart.
 */
public class SalesPanel extends JPanel {

    // Components
    private JTextField textSalesSearch;
    private JButton btnSalesSearch;
    private JTable tblSalesProducts;
    private JSpinner spinnerQuantity;
    private JButton btnAddToCart;

    private JTable tblCart;
    private JLabel lblTotal;
    private JButton btnRemoveFromCart;
    private JButton btnCheckout;

    public SalesPanel() {
        setLayout(new GridLayout(1, 2, 15, 15));
        setBackground(Theme.PANEL_COLOR);

        add(createCatalogPanel());
        add(createCartPanel());
    }

    /**
     * Creates the left side; Product search and selection
     */
    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Theme.PANEL_COLOR);
        panel.setBorder(UIStyles.createPanelBorder("Catalogo de Productos"));

        // Top: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Theme.PANEL_COLOR);
        searchPanel.add(new JLabel("Buscar:"));
        textSalesSearch = UIStyles.createTextField();
        btnSalesSearch = UIStyles.createButton("Buscar", Theme.PRIMARY_COLOR);
        btnSalesSearch.setPreferredSize(new Dimension(100, 30));

        searchPanel.add(textSalesSearch);
        searchPanel.add(btnSalesSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Center: Table
        String[] cols = {"ID", "Nombre", "Precio", "Stock"};
        tblSalesProducts = new JTable(new Object[0][0], cols);
        tblSalesProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSalesProducts.setRowHeight(32);
        JScrollPane scroll = UIStyles.createScrollPane(tblSalesProducts);
        panel.add(scroll, BorderLayout.CENTER);

        // Botton: Add to Cart
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addPanel.setBackground(Theme.PANEL_COLOR);
        addPanel.add(new JLabel("Cantidad:"));
        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerQuantity.setPreferredSize(new Dimension(80, 30));
        btnAddToCart = UIStyles.createButton("Agregar al Carrito", Theme.SUCCESS_COLOR);
        addPanel.add(spinnerQuantity);
        addPanel.add(btnAddToCart);
        panel.add(addPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the right side: Shopping cart and checkout.
     */
    private JPanel createCartPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Theme.PANEL_COLOR);
        panel.setBorder(UIStyles.createPanelBorder("Carrito de Compras"));

        // Center: Cart Table
        String[] cols = {"ID", "Nombre", "Cantidad", "Subtotal"};
        tblCart = new JTable(new Object[0][0], cols);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCart.setRowHeight(32);
        JScrollPane scroll = UIStyles.createScrollPane(tblCart);
        panel.add(scroll, BorderLayout.CENTER);

        // Botton: Checkout
        JPanel checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setBackground(Theme.PANEL_COLOR);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(Theme.DANGER_COLOR);
        checkoutPanel.add(lblTotal, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 10));
        buttons.setBackground(Theme.PANEL_COLOR);
        btnRemoveFromCart = UIStyles.createButton("Quitar Item", Theme.SECONDARY_COLOR);
        btnCheckout = UIStyles.createButton("Finalizar Compra", Theme.SUCCESS_COLOR);
        buttons.add(btnRemoveFromCart);
        buttons.add(btnCheckout);
        checkoutPanel.add(buttons, BorderLayout.CENTER);

        panel.add(checkoutPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Getters for the Controller
    public JTextField getTextSalesSearch() {
        return textSalesSearch;
    }

    public JButton getBtnSalesSearch() {
        return btnSalesSearch;
    }

    public JTable getTblSalesProducts() {
        return tblSalesProducts;
    }

    public JSpinner getSpinnerQuantity() {
        return spinnerQuantity;
    }

    public JButton getBtnAddToCart() {
        return btnAddToCart;
    }

    public JTable getTblCart() {
        return tblCart;
    }

    public JLabel getLblTotal() {
        return lblTotal;
    }

    public JButton getBtnRemoveFromCart() {
        return btnRemoveFromCart;
    }

    public JButton getBtnCheckout() {
        return btnCheckout;
    }
}
