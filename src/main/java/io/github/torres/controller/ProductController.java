package io.github.torres.controller;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import io.github.torres.dao.ProductDAO;
import io.github.torres.dao.ProductDAO.DAOException;
import io.github.torres.model.Product;
import io.github.torres.view.MainView;
import io.github.torres.view.panels.InventoryPanel;
import io.github.torres.view.panels.SalesPanel;

/**
 * Orchestrates the data flow between the {@link MainView} (GUI) and the {@link ProductDAO}
 * (database).
 *
 * <p>
 * Represents the CONTROLLER layer in the MVC pattern. All Swing interactions are performed on the
 * Event Dispatch Thread.
 * </p>
 */
public class ProductController {

    private final MainView view;
    private final ProductDAO productDAO;
    private final InventoryPanel inventoryPanel;
    private final SalesPanel salesPanel;
    private double cartTotal = 0.0;

    // Variable to remember which ID we are currently editing
    private Integer currentEditingId = null;

    /**
     * Wires the view and DAO together and performs an initial data load.
     *
     * @param view the main application window.
     * @param productDAO the data-access object for products.
     */
    public ProductController(MainView view, ProductDAO productDAO) {

        this.view = view;
        this.productDAO = productDAO;

        this.inventoryPanel = view.getInventoryPanel();
        this.salesPanel = view.getSalesPanel();
        registerListeners();

        // Initial load of data when the application starts
        refreshTable();
    }

    /**
     * Register all UI listeners.
     */
    private void registerListeners() {

        // Buttons
        inventoryPanel.getBtnAdd().addActionListener(e -> addProduct());

        inventoryPanel.getBtnDelete().addActionListener(e -> deleteProduct());

        inventoryPanel.getBtnUpdate().addActionListener(e -> updateProduct());

        inventoryPanel.getBtnSearch().addActionListener(e -> searchProduct());

        inventoryPanel.getBtnClearSearch().addActionListener(e -> clearSearchAndFilters());

        inventoryPanel.getComboFilter().addActionListener(e -> applyFilter());

        // Table Selection Listener
        inventoryPanel.getTblProducts().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadProductIntoForm();
            }
        });

        salesPanel.getBtnSalesSearch().addActionListener(e -> searchSalesProduct());

        salesPanel.getBtnAddToCart().addActionListener(e -> addToCart());

        salesPanel.getBtnRemoveFromCart().addActionListener(e -> removeFromCart());

        salesPanel.getBtnCheckout().addActionListener(e -> checkout());

        initCartTable();
    }

    /**
     * Helper method: Renders any given list of products into the JTable Prevents code duplication
     * when refreshing, searching, or filtering.
     */
    private void renderTableData(List<Product> products) {
        String[] columns = {"ID", "Nombre", "Precio", "Stock", "Descripción"};

        // DefaultTableModel allows us to dynamically alter rows
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate the data matrix
        for (Product p : products) {
            Object[] row = {p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getDescription()};
            model.addRow(row);
        }
        inventoryPanel.getTblProducts().setModel(model);
    }

    /**
     * Fetches all products from MySQL and updates the JTable view.
     */
    private void refreshTable() {

        try {

            List<Product> products = productDAO.getAll();

            renderTableData(products);
            renderSalesCatalog(products);

        } catch (DAOException ex) {
            showError("No se pudo cargar la lista de productos.\n" + ex.getMessage());
        }
    }

    /**
     * Retrieves products based on the search keyword and updates the table.
     */
    private void searchProduct() {
        String keyWord = inventoryPanel.getSearchText();
        if (keyWord.isEmpty()) {
            refreshTable();
            return;
        }

        try {
            List<Product> results = productDAO.search(keyWord);
            renderTableData(results);
        } catch (DAOException ex) {
            showError("Error en la busqueda.\n" + ex.getMessage());
        }
    }

    /**
     * Applies the selected filter from the combo box and updates the table.
     */
    private void applyFilter() {
        int selctedIndex = inventoryPanel.getComboFilter().getSelectedIndex();

        try {
            List<Product> results;
            switch (selctedIndex) {
                case 1:
                    // Highest Price (Desending)
                    results = productDAO.sortByPrice(false);
                    break;
                case 2:
                    // Lowest Price (Asending)
                    results = productDAO.sortByPrice(true);
                    break;
                case 3:
                    // Out of Stock
                    results = productDAO.getOutOfStock();
                    break;
                default:
                    // All (Default case 0)
                    results = productDAO.getAll();
                    break;
            }
            renderTableData(results);
        } catch (DAOException ex) {
            showError("Error al aplicar filtro.\n" + ex.getMessage());
        }
    }

    /**
     * Clears the search field, resets the filter, and reloads all data.
     */
    private void clearSearchAndFilters() {
        inventoryPanel.setSearchText("");
        inventoryPanel.getComboFilter().setSelectedIndex(0); // Resets the filter to "Todos"
        refreshTable();
    }

    /**
     * Populates the forms fields with data from the currently selected product in the table,
     * enabling edit mode for updating an existing record.
     */
    private void loadProductIntoForm() {
        JTable table = inventoryPanel.getTblProducts();
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            currentEditingId = (int) table.getValueAt(selectedRow, 0);

            // We fill the text boxes with the data from the row
            inventoryPanel.setProductName(table.getValueAt(selectedRow, 1).toString());
            inventoryPanel.setProductPrice(table.getValueAt(selectedRow, 2).toString());
            inventoryPanel.setProductStock(table.getValueAt(selectedRow, 3).toString());
            inventoryPanel.setProductDescription(table.getValueAt(selectedRow, 4).toString());

            // We turn off Add and turn on Save Changes
            inventoryPanel.getBtnAdd().setEnabled(false);
            inventoryPanel.getBtnUpdate().setEnabled(true);
        }
    }

    /**
     * Gathers inputs from the form, validates them, and saves a new product to the databese.
     */
    private void addProduct() {
        String name = inventoryPanel.getProductName();
        String description = inventoryPanel.getProductDescription();
        String priceStr = inventoryPanel.getProductPrice();
        String stockStr = inventoryPanel.getProductStock();

        // Basic Defensive Validation
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, rellene todos los campos obligatorios (Nombre, Precio, Stock)",
                    "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double price;
        Integer stock;

        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view,
                    "Formato de datos no válido. El precio debe ser decimal y el stock un número entero.",
                    "Error de formato de datos", JOptionPane.WARNING_MESSAGE);
            return;

        }
        if (price < 0 || stock < 0) {
            JOptionPane.showMessageDialog(view, "El precio y el stock deben ser números positivos.",
                    " Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Product product = new Product(null, name, description, price, stock);
            productDAO.save(product);
            JOptionPane.showMessageDialog(view, "Producto agregado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            inventoryPanel.clearFields();
            inventoryPanel.getTblProducts().clearSelection();
            refreshTable();
        } catch (DAOException ex) {
            showError("No se pudo guardar el producto.\n" + ex.getMessage());
        }
    }

    /**
     * Updates an existing product using the form data.
     */
    private void updateProduct() {
        if (currentEditingId == null)
            return;

        String name = inventoryPanel.getProductName();
        String description = inventoryPanel.getProductDescription();
        String priceStr = inventoryPanel.getProductPrice();
        String stockStr = inventoryPanel.getProductStock();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            showError("Faltan campos obligatorios.");
            return;
        }
        try {
            Double price = Double.parseDouble(priceStr);
            Integer stock = Integer.parseInt(stockStr);
            if (price < 0 || stock < 0) {
                showError("El precio y el stock deben ser positivos");
                return;
            }

            // We assemble the product with the saved ID
            Product product = new Product(currentEditingId, name, description, price, stock);
            productDAO.update(product);

            JOptionPane.showMessageDialog(view, "Producto actualizado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // We reset the visual state
            resetFormState();

            refreshTable();
        } catch (NumberFormatException ex) {
            showError("Formato númerico inválido.");
        } catch (DAOException ex) {
            showError("No se puede actualizar el producto.\n" + ex.getMessage());
        }
    }

    /**
     * Identifies the selected row in the table and removes it from the database.
     */
    private void deleteProduct() {
        JTable table = inventoryPanel.getTblProducts();
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un producto de la tabla a eliminar.",
                    "Error de Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extract the value from the ID column (Column index 0)
        int id = (int) (table.getValueAt(selectedRow, 0));

        int confirm = JOptionPane.showConfirmDialog(view,
                "¿Está seguro que desea eliminar este producto?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productDAO.delete(id);
                JOptionPane.showMessageDialog(view, "Producto eliminado correctamente.",
                        " Eliminado", JOptionPane.INFORMATION_MESSAGE);
                resetFormState();
                refreshTable();
            } catch (DAOException ex) {
                showError("No se pudo eliminar el producto.\n" + ex.getMessage());
            }
        }
    }

    /**
     * Restores the form to creation mode.
     */
    private void resetFormState() {
        currentEditingId = null;

        inventoryPanel.clearFields();

        inventoryPanel.getBtnAdd().setEnabled(true);

        inventoryPanel.getBtnUpdate().setEnabled(false);

        inventoryPanel.getTblProducts().clearSelection();
    }

    /**
     * Displays a modal error dialog with the given message.
     *
     * @param message the localised (Spanish) error description shown to the user.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ====================================
    // POINTS OF SALE (POS) LOGIC
    // ====================================

    /**
     * Initializes the empty shopping cart table.
     */
    private void initCartTable() {

        String[] cartCols = {"ID", "Nombre", "Cantidad", "Subtotal"};
        DefaultTableModel cartModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesPanel.getTblCart().setModel(cartModel);
    }

    /**
     * Renders products into the POS catalog table.
     */
    private void renderSalesCatalog(List<Product> products) {
        String[] columns = {"ID", "Nombre", "Precio", "Stock"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Product product : products) {
            Object[] row =
                    {product.getId(), product.getName(), product.getPrice(), product.getStock()};
            model.addRow(row);
        }
        salesPanel.getTblSalesProducts().setModel(model);
    }

    private void searchSalesProduct() {
        String keyword = salesPanel.getTextSalesSearch().getText().trim();
        try {
            List<Product> results =
                    keyword.isEmpty() ? productDAO.getAll() : productDAO.search(keyword);
            renderSalesCatalog(results);
        } catch (DAOException ex) {
            showError("Error al buscar en catálogo.\n" + ex.getMessage());
        }
    }

    private void addToCart() {
        JTable catalogTable = salesPanel.getTblSalesProducts();
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un producto para agregar.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) catalogTable.getValueAt(selectedRow, 0);
        String name = catalogTable.getValueAt(selectedRow, 1).toString();
        double price = (double) catalogTable.getValueAt(selectedRow, 2);
        int stock = (int) catalogTable.getValueAt(selectedRow, 3);
        int quantity = (int) salesPanel.getSpinnerQuantity().getValue();

        if (quantity > stock) {
            JOptionPane.showMessageDialog(view, "Stock insuficiente. Disponible: " + stock, "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = price * quantity;
        DefaultTableModel cartModel = (DefaultTableModel) salesPanel.getTblCart().getModel();
        cartModel.addRow(new Object[] {id, name, quantity, subtotal});

        cartTotal += subtotal;
        salesPanel.getLblTotal().setText(String.format("Total: $%.2f", cartTotal));
    }

    private void removeFromCart() {
        JTable carTable = salesPanel.getTblCart();
        int selctedRow = carTable.getSelectedRow();

        if (selctedRow == -1)
            return;

        double subtotal = (double) carTable.getValueAt(selctedRow, 3);
        ((DefaultTableModel) carTable.getModel()).removeRow(selctedRow);

        cartTotal -= subtotal;
        if (cartTotal < 0.01)
            cartTotal = 0.00;
        salesPanel.getLblTotal().setText(String.format("Total: $%.2f", cartTotal));
    }

    /**
     * Processes the checkout, updating the database inventory and clearing the cart.
     */
    private void checkout() {
        DefaultTableModel cartModel = (DefaultTableModel) salesPanel.getTblCart().getModel();
        int rowsCount = cartModel.getRowCount();

        // Prevent checkout if cart is empty
        if (rowsCount == 0) {
            JOptionPane.showMessageDialog(view, "El carrito está vacío.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int count = rowsCount;
            // Iterate through all cart items and deduct stock in the database
            for (int i = 0; i < count; i++) {
                int productId = (int) cartModel.getValueAt(i, 0);
                int quantity = (int) cartModel.getValueAt(i, 2);

                productDAO.reduceStock(productId, quantity);
            }

            // Provide visual feedback for successfull transaction
            JOptionPane.showMessageDialog(view, "¡Compra finalizada con éxito!", "Venta Completada",
                    JOptionPane.INFORMATION_MESSAGE);

            // Reset the POS UI state completely
            cartModel.setRowCount(0);
            cartTotal = 0.0;
            salesPanel.getLblTotal().setText("Total: $0.00");
            salesPanel.getSpinnerQuantity().setValue(1);

            // Refresh all tables (Inventory and POS Catalog) to reflect the new store
            refreshTable();
        } catch (DAOException ex) {
            showError("Error al procesar la compra.\n" + ex.getMessage());
        }
    }
}
