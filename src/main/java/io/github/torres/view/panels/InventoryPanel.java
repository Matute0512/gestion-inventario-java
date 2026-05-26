package io.github.torres.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import io.github.torres.view.styles.Theme;
import io.github.torres.view.styles.UIStyles;


/**
 * Inventory management panel.
 *
 * <p>
 * Contains:
 * </p>
 * <ul>
 * <li>Product form</li>
 * <li>Search and filters</li>
 * <li>Products table</li>
 * <li>CRUD action buttons</li>
 * </ul>
 */

public class InventoryPanel extends JPanel {

    // Components
    private JTextField textName;
    private JTextField textPrice;
    private JTextField textStock;

    private JTextArea textDescription;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    private JTable tblProducts;

    private JTextField textSearch;

    private JButton btnSearch;
    private JButton btnClearSearch;

    private JComboBox<String> comboFilter;

    /*
     * Creates the inventory panel.
     */
    public InventoryPanel() {

        setLayout(new BorderLayout(15, 15));

        setBackground(Theme.BACKGROUND_COLOR);

        add(createFormPanel(), BorderLayout.WEST);

        add(createTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Creates the product form panel.
     *
     * @return configured form panel
     */
    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(Theme.PANEL_COLOR);

        panel.setPreferredSize(new Dimension(360, 0));

        panel.setBorder(UIStyles.createPanelBorder("Detalles del Producto"));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        fieldsPanel.setBackground(Theme.PANEL_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);

        // Name
        addLabel(fieldsPanel, "Nombre:", gbc, 0);

        textName = UIStyles.createTextField();

        addField(fieldsPanel, textName, gbc, 0);

        // Price
        addLabel(fieldsPanel, "Precio ($):", gbc, 1);

        textPrice = UIStyles.createTextField();

        addField(fieldsPanel, textPrice, gbc, 1);

        // Stock
        addLabel(fieldsPanel, "Stock:", gbc, 2);

        textStock = UIStyles.createTextField();

        addField(fieldsPanel, textStock, gbc, 2);

        // Description
        addLabel(fieldsPanel, "Descripción:", gbc, 3);

        textDescription = UIStyles.createTextArea();

        JScrollPane descriptionScroll = UIStyles.createScrollPane(textDescription);

        descriptionScroll.setPreferredSize(new Dimension(180, 120));

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;

        gbc.fill = GridBagConstraints.BOTH;

        fieldsPanel.add(descriptionScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 8, 8));

        buttonPanel.setBackground(Theme.PANEL_COLOR);

        btnAdd = UIStyles.createButton("Agregar Producto", Theme.SUCCESS_COLOR);

        btnUpdate = UIStyles.createButton("Guardar cambios", Theme.PRIMARY_COLOR);

        btnUpdate.setEnabled(false);

        buttonPanel.add(btnAdd);

        buttonPanel.add(btnUpdate);

        gbc.gridx = 0;

        gbc.gridy = 4;

        gbc.gridwidth = 2;

        gbc.weightx = 1;

        gbc.weighty = 0;

        gbc.fill = GridBagConstraints.HORIZONTAL;

        fieldsPanel.add(buttonPanel, gbc);

        // Spacer
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

        panel.setBackground(Theme.PANEL_COLOR);

        panel.setBorder(UIStyles.createPanelBorder("Inventario y Stock"));

        // Top Panel

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        topPanel.setBackground(Theme.PANEL_COLOR);

        topPanel.add(new JLabel("Buscar:"));

        textSearch = new JTextField(15);

        btnSearch = UIStyles.createButton("Buscar", Theme.PRIMARY_COLOR);

        btnClearSearch = UIStyles.createButton("Limpiar", Theme.SECONDARY_COLOR);

        btnSearch.setPreferredSize(new Dimension(120, 35));

        btnClearSearch.setPreferredSize(new Dimension(120, 35));

        topPanel.add(textSearch);

        topPanel.add(btnSearch);

        topPanel.add(btnClearSearch);

        topPanel.add(new JLabel("Filtrar por:"));

        String[] filterOptions = {"Todos", "Mayor Precio", "Menor Precio", "Sin Stock"};

        comboFilter = new JComboBox<>(filterOptions);

        topPanel.add(comboFilter);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table

        String[] columns = {"ID", "Nombre", "Precio", "Stock", "Descripción"};

        tblProducts = new JTable(new Object[0][0], columns);

        configureTable(tblProducts);

        JScrollPane scrollPane = UIStyles.createScrollPane(tblProducts);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottons Panel

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        controlPanel.setBackground(Theme.PANEL_COLOR);

        btnDelete = UIStyles.createButton("Eliminar Producto", Theme.DANGER_COLOR);

        controlPanel.add(btnDelete);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Apllies configuration to the products table
     *
     * @param table target table
     */
    private void configureTable(JTable table) {

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setAutoCreateRowSorter(true);

        table.setRowHeight(32);

        table.setShowGrid(true);

        table.setGridColor(Theme.BORDER_COLOR);

        table.setIntercellSpacing(new Dimension(1, 1));

        table.setFillsViewportHeight(true);

        table.setSelectionBackground(Theme.PRIMARY_COLOR);

        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRender);

        table.getColumnModel().getColumn(2).setCellRenderer(centerRender);

        table.getColumnModel().getColumn(3).setCellRenderer(centerRender);

    }

    // Form Helpers

    /**
     * Adds a label to the form.
     *
     * @param panel target panel
     * @param text label text
     * @param gbc layout constraints
     * @param y row position
     */
    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int y) {

        gbc.gridx = 0;

        gbc.gridy = y;

        gbc.weightx = 0;

        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(new JLabel(text), gbc);
    }

    /**
     * Adds an input component to the form.
     *
     * @param panel target panel
     * @param component input component
     * @param gbc layout constraints
     * @param y row position
     */
    private void addField(JPanel panel, JComponent component, GridBagConstraints gbc, int y) {

        gbc.gridx = 1;

        gbc.gridy = y;

        gbc.weightx = 1;

        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(component, gbc);
    }

    // Getters
    public String getProductName() {
        return textName.getText().trim();
    }

    public String getProductPrice() {
        return textPrice.getText().trim();
    }

    public String getProductStock() {
        return textStock.getText().trim();
    }

    public String getProductDescription() {
        return textDescription.getText().trim();
    }

    public String getSearchText() {
        return textSearch.getText().trim();
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnDelete() {
        return btnDelete;
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

    // Setters
    public void setProductName(String text) {
        textName.setText(text);
    }

    public void setProductPrice(String text) {
        textPrice.setText(text);
    }

    public void setProductStock(String text) {
        textStock.setText(text);
    }

    public void setProductDescription(String text) {
        textDescription.setText(text);
    }

    public void setSearchText(String text) {
        textSearch.setText(text);
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
