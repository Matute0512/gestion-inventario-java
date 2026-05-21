package io.github.torres.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Main window for the Inventory Management application.
 * Represents the VIEW layer in the MVC pattern.
 */
public class MainView extends JFrame {

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_COLOR = Color.WHITE;

    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color SECONDARY_COLOR = new Color(127, 140, 141);

    // Components
    private JTextField textName, textPrice, textStock;
    private JTextArea textDescription;

    private JButton btnAdd, btnDelete, btnUpdate;

    private JTable tblProducts;

    private JTextField textSearch;
    private JButton btnSearch, btnClearSearch;
    private JComboBox<String> comboFilter;

    private JTabbedPane tabbedPane;

    // Sales Module Components

    private JTable tblSalesProducts;
    private JTable tblCart;
    private JTextField textSalesSearch;
    private JButton btnSalesSearch, btnAddToCart, btnRemoveFromCart, btnCheckout;
    private JSpinner spinnerQuantity;
    private JLabel lblTotal;

    /**
     * Builds and lays out all Swing components.
     */
    public MainView() {

        // Window Configuration
        setTitle("SGE - Gestión de Inventario");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Layout for the Window
        setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Initialize the tabbed pane system
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Inventory Management (Existing CRUD)
        JPanel inventoryTab = new JPanel(new BorderLayout(15, 15));
        inventoryTab.setBackground(BACKGROUND_COLOR);

        // Add existing panels
        inventoryTab.add(createFormPanel(), BorderLayout.WEST);
        inventoryTab.add(createTablePanel(), BorderLayout.CENTER);

        // POINT OF SALE
        JPanel salesTab = createSalesPanel();

        // Add tabs to the Container
        tabbedPane.addTab("Gestión de Inventario", inventoryTab);
        tabbedPane.addTab("Punto de Venta", salesTab);

        // Add the tabbed container to the center of the main window
        add(tabbedPane, BorderLayout.CENTER);

    }

    /**
     * Creates the form panel for product data input.
     *
     * @return configured form panel
     */
    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(PANEL_COLOR);

        panel.setPreferredSize(new Dimension(340, 0));

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        "Detalles del Producto"));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        fieldsPanel.setBackground(PANEL_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Border roundedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));

        Dimension inputSize = new Dimension(180, 38);
        // ---------- NAME ----------
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        fieldsPanel.add(new JLabel("Nombre:"), gbc);

        textName = new JTextField();
        textName.setBorder(roundedBorder);
        textName.setPreferredSize(inputSize);

        gbc.gridx = 1;
        gbc.weightx = 1;

        fieldsPanel.add(textName, gbc);

        // ---------- PRICE ----------
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        fieldsPanel.add(new JLabel("Precio ($):"), gbc);

        textPrice = new JTextField();
        textPrice.setBorder(roundedBorder);
        textPrice.setPreferredSize(inputSize);
        gbc.gridx = 1;
        gbc.weightx = 1;

        fieldsPanel.add(textPrice, gbc);

        // ---------- STOCK ----------
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        fieldsPanel.add(new JLabel("Stock:"), gbc);

        textStock = new JTextField();
        textStock.setBorder(roundedBorder);
        textStock.setPreferredSize(inputSize);
        gbc.gridx = 1;
        gbc.weightx = 1;

        fieldsPanel.add(textStock, gbc);

        // ---------- DESCRIPTION ----------
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        fieldsPanel.add(new JLabel("Descripción:"), gbc);

        textDescription = new JTextArea(6, 15);

        textDescription.setLineWrap(true);
        textDescription.setWrapStyleWord(true);

        textDescription.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane descriptionScroll = new JScrollPane(textDescription);
        descriptionScroll.setPreferredSize(new Dimension(180, 120));
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        fieldsPanel.add(descriptionScroll, gbc);

        // ---------- BUTTONS ----------
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 8, 8));

        buttonPanel.setBackground(PANEL_COLOR);

        btnAdd = new JButton("Agregar Producto");
        btnUpdate = new JButton("Guardar Cambios");

        btnUpdate.setEnabled(false);

        styleButton(btnAdd, SUCCESS_COLOR);
        styleButton(btnUpdate, PRIMARY_COLOR);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fieldsPanel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fieldsPanel.add(buttonPanel, gbc);

        // Spacer to push everything upward
        gbc.gridy = 5;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        fieldsPanel.add(Box.createVerticalGlue(), gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the inventory table panel.
     *
     * @return configured table panel
     */
    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(PANEL_COLOR);

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        "Inventario y Stock"));

        // ---------- TOP PANEL ----------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        topPanel.setBackground(PANEL_COLOR);

        topPanel.add(new JLabel("Buscar:"));

        textSearch = new JTextField(15);

        btnSearch = new JButton("Buscar");
        btnClearSearch = new JButton("Limpiar");

        styleButton(btnSearch, PRIMARY_COLOR);
        styleButton(btnClearSearch, SECONDARY_COLOR);

        topPanel.add(textSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnClearSearch);

        topPanel.add(new JLabel("Filtrar por:"));

        String[] filterOptions = {
                "Todos",
                "Mayor Precio",
                "Menor Precio",
                "Sin Stock"
        };

        comboFilter = new JComboBox<>(filterOptions);

        topPanel.add(comboFilter);

        panel.add(topPanel, BorderLayout.NORTH);

        // ---------- TABLE ----------
        String[] columns = {
                "ID",
                "Nombre",
                "Precio",
                "Stock",
                "Descripción"
        };

        tblProducts = new JTable(new Object[0][0], columns);

        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblProducts.setAutoCreateRowSorter(true);

        tblProducts.setRowHeight(32);

        tblProducts.setShowGrid(true);

        tblProducts.setGridColor(new Color(220, 220, 220));

        tblProducts.setIntercellSpacing(new Dimension(1, 1));

        tblProducts.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tblProducts);

        panel.add(scrollPane, BorderLayout.CENTER);

        // ---------- BOTTOM PANEL ----------
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        controlPanel.setBackground(PANEL_COLOR);

        btnDelete = new JButton("Eliminar Producto");

        styleButton(btnDelete, DANGER_COLOR);

        controlPanel.add(btnDelete);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Applies modern styling to buttons.
     *
     * @param button button to style
     * @param color  background color
     */
    private void styleButton(JButton button, Color color) {

        button.setBackground(color);

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setPreferredSize(new Dimension(180, 40));
    }

    /**
     * Creates the Point of Sale (POS) pane.
     * Divided into Product Selection (left) and Shopping Cart (right).
     *
     * @return configured sales panel
     */
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT SIDE: PRODUCT CATALOG & SELECTION
        JPanel catalogPanel = new JPanel(new BorderLayout(10, 10));
        catalogPanel.setBackground(BACKGROUND_COLOR);
        catalogPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "Catalogo de Productos"));

        // top: Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(PANEL_COLOR);
        textSalesSearch = new JTextField(15);
        btnSalesSearch = new JButton("Buscar");
        styleButton(btnSalesSearch, PRIMARY_COLOR);
        btnSalesSearch.setPreferredSize(new Dimension(100, 35));
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(textSalesSearch);
        searchPanel.add(btnSalesSearch);
        catalogPanel.add(searchPanel, BorderLayout.NORTH);

        // Center: Available Products Table
        String[] catalogCols = { "ID", "Nombre", "Precio", "Stock" };
        tblSalesProducts = new JTable(new Object[0][0], catalogCols);
        tblProducts.setRowHeight(28);
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane catalogScroll = new JScrollPane(tblSalesProducts);
        catalogPanel.add(catalogScroll, BorderLayout.CENTER);

        // Botton: Add to Cart Controls
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addPanel.setBackground(PANEL_COLOR);
        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerQuantity.setPreferredSize(new Dimension(80, 35));
        btnAddToCart = new JButton("Agregar al Carrito");
        styleButton(btnAddToCart, PRIMARY_COLOR);
        addPanel.add(spinnerQuantity);
        addPanel.add(btnAddToCart);
        catalogPanel.add(addPanel, BorderLayout.SOUTH);

        // RIGHT SIDE: SHOPPING CART & CHECKOUT
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBackground(PANEL_COLOR);
        cartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "Carrito de Compras"));

        // Center: Cart Table
        String[] cartCols = { "ID", "Nombre", "Cantidad", "Subtotal" };
        tblCart = new JTable(new Object[0][0], cartCols);
        tblCart.setRowHeight(28);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cartScroll = new JScrollPane(tblCart);
        cartPanel.add(cartScroll, BorderLayout.CENTER);

        // Button: Total & Checkout Controls
        JPanel checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setBackground(PANEL_COLOR);
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total Label
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(DANGER_COLOR);
        checkoutPanel.add(lblTotal, BorderLayout.NORTH);

        // Buttons: Remove item & Checkout
        JPanel checkoutButtons = new JPanel(new GridLayout(1, 2, 10, 10));
        checkoutButtons.setBackground(PANEL_COLOR);
        btnRemoveFromCart = new JButton("Quitar Item");
        styleButton(btnRemoveFromCart, SECONDARY_COLOR);
        btnCheckout = new JButton("Finalizar Compra");
        styleButton(btnCheckout, SUCCESS_COLOR);
        checkoutButtons.add(btnRemoveFromCart);
        checkoutButtons.add(btnCheckout);

        checkoutPanel.add(checkoutButtons, BorderLayout.CENTER);
        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        panel.add(catalogPanel);
        panel.add(cartPanel);

        return panel;
    }

    // GETTERS
    public String getTextName() {
        return textName.getText().trim();
    }

    public String getTextPrice() {
        return textPrice.getText().trim();
    }

    public String getTextStock() {
        return textStock.getText().trim();
    }

    public String getTextDescription() {
        return textDescription.getText().trim();
    }

    public String getTextSearch() {
        return textSearch.getText().trim();
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JTable getTblProducts() {
        return tblProducts;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }

    public JButton getBtnClearSearch() {
        return btnClearSearch;
    }

    public JComboBox<String> getComboFilter() {
        return comboFilter;
    }

    // SETTERS
    public void setTextName(String text) {
        textName.setText(text);
    }

    public void setTextPrice(String text) {
        textPrice.setText(text);
    }

    public void setTextStock(String text) {
        textStock.setText(text);
    }

    public void setTextDescription(String text) {
        textDescription.setText(text);
    }

    public void setTextSearch(String text) {
        textSearch.setText(text);
    }

    // --- SALES GETTERS ---
    public JTable getTblSalesProducts() {
        return tblSalesProducts;
    }

    public JTable getTblCart() {
        return tblCart;
    }

    public JTextField getTextSalesSearch() {
        return textSalesSearch;
    }

    public JButton getBtnSalesSearch() {
        return btnSalesSearch;
    }

    public JButton getBtnAddToCart() {
        return btnAddToCart;
    }

    public JButton getBtnRemoveFromCart() {
        return btnRemoveFromCart;
    }

    public JButton getBtnCheckout() {
        return btnCheckout;
    }

    public JSpinner getSpinnerQuantity() {
        return spinnerQuantity;
    }

    public JLabel getLblTotal() {
        return lblTotal;
    }

    /**
     * Clears all form fields.
     */
    public void clearFields() {

        textName.setText("");
        textPrice.setText("");
        textStock.setText("");
        textDescription.setText("");
    }
}
