package io.github.torres.controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.Cursor;
import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;
import javax.swing.table.DefaultTableModel;
import io.github.torres.dao.ProductDAO;
import io.github.torres.dao.ProductDAO.DAOException;
import io.github.torres.model.CartItem;
import io.github.torres.model.Product;
import io.github.torres.view.MainView;
import io.github.torres.view.panels.InventoryPanel;
import io.github.torres.view.panels.SalesPanel;
import io.github.torres.view.styles.Theme;
import io.github.torres.exception.ValidationException;
import io.github.torres.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
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
        view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        inventoryPanel.getBtnAdd().setEnabled(false);

        SwingWorker<List<Product>,Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            // 1. THIS RUNS IN A BACKGROUND THREAD
            protected List<Product> doInBackground(){
                return productDAO.getAll();
            }

            @Override
            protected void done() {
                try{
                    List<Product> products = get();

                    renderTableData(products);
                    renderSalesCatalog(products);
                    logger.info("Productos cargados exitosamente. Total: {}", products.size());
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error al cargar productos en background.", e);
                    showError("No se pudo cargar la lista de productos.\n" + e.getMessage());
                } finally{
                    // We restore the UI state regardless of whether there was an error or success
                    view.setCursor(Cursor.getDefaultCursor());
                    inventoryPanel.getBtnAdd().setEnabled(true);
                }
            }
        };
        worker.execute();
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
        int selectedIndex = inventoryPanel.getComboFilter().getSelectedIndex();

        try {
            List<Product> results;
            switch (selectedIndex) {
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
        logger.debug("Intentando agregar nuevo producto...");
        try{
            String name = inventoryPanel.getProductName();
            String description = inventoryPanel.getProductDescription();
            String priceStr = inventoryPanel.getProductPrice();
            String stockStr = inventoryPanel.getProductStock();

            // Validate all fields using ValidationUtil
            double[] values = ValidationUtil.validateProductFields(name,description,priceStr,stockStr);
            Double price = values[0];
            Integer stock = (int) values[1];

            // Create and save product
            Product product = new Product(null,name,description,price,stock);
            productDAO.save(product);

            logger.info("✅ Producto agregado correctamente: {}", name);
            JOptionPane.showMessageDialog(view, "Producto agregado correctamente","Exito",JOptionPane.INFORMATION_MESSAGE);

            inventoryPanel.clearFields();
            inventoryPanel.getTblProducts().clearSelection();
            refreshTable();

        } catch (ValidationException ex) {
            logger.warn("Error de validacion en addProduct: {}",ex.getMessage());
            showError(ex.getMessage());
        } catch (DAOException ex) {
            logger.error("❌ Error al guardar producto", ex);
            showError("No se pudo guardar el producto.\n" + ex.getMessage());
        }

    }

    /**
     * Updates an existing product using the form data.
     */
    private void updateProduct() {
        logger.debug("Intentando actualizar producto. ID: {}", currentEditingId);
        if (currentEditingId == null){
            logger.warn("Intento de actualizar sin seleccionar producto");
            return;
        }

        try{
            String name = inventoryPanel.getProductName();
            String description = inventoryPanel.getProductDescription();
            String priceStr = inventoryPanel.getProductPrice();
            String stockStr = inventoryPanel.getProductStock();

            // Validate all fields using ValidationUtil
            double[] values = ValidationUtil.validateProductFields(name,description,priceStr,stockStr);
            Double price = values[0];
            Integer stock = (int) values[1];

            // Update and save product
            Product product = new Product(currentEditingId,name,description,price,stock);
            productDAO.update(product);

            logger.info("✅ Producto actualizado correctamente. ID: {}", currentEditingId);
            JOptionPane.showMessageDialog(view, "Producto actualizado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            resetFormState();
            refreshTable();

        } catch (ValidationException ex) {
            logger.warn("Error de validacion en updateProduct: {}", ex.getMessage());
            showError(ex.getMessage());
        } catch (DAOException ex) {
            logger.error("❌ Error al actualizar producto: {}", currentEditingId, ex);
            showError("No se pudo actualizar el producto.\n" + ex.getMessage());
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
        int selectedRow = carTable.getSelectedRow();

        if (selectedRow == -1)
            return;

        double subtotal = (double) carTable.getValueAt(selectedRow, 3);
        ((DefaultTableModel) carTable.getModel()).removeRow(selectedRow);

        cartTotal -= subtotal;
        if (cartTotal < Theme.CART_TOTAL_EPSILON)
            cartTotal = 0.00;
        salesPanel.getLblTotal().setText(String.format("Total: $%.2f", cartTotal));
    }

    /**
     * Processes the checkout, updating the database inventory and clearing the cart.
     */
    private void checkout() {
        logger.info("Iniciando proceso de checkout");

        DefaultTableModel cartModel = (DefaultTableModel) salesPanel.getTblCart().getModel();
        int rowsCount = cartModel.getRowCount();

        // Prevent checkout if cart is empty
        if (rowsCount == 0) {
            logger.warn("Intento de checkout con carrito vacío.");
            JOptionPane.showMessageDialog(view, "El carrito está vacío.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Prepare cart items for transaction
            List<CartItem> cartItems = new ArrayList<>();
            for  (int i = 0; i < rowsCount; i++) {
                int productId = (int) cartModel.getValueAt(i, 0);
                int quantity = (int) cartModel.getValueAt(i, 2);
                double subtotal = (double) cartModel.getValueAt(i, 3);

                cartItems.add(new CartItem(productId, quantity, subtotal));
            }
            // Register sale with transaction
            productDAO.registerSale(cartTotal,cartItems);

            logger.info("Venta completada exitosamente. Total: ${}",cartTotal);
            // Provide visual feedback for successfully transaction
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
            logger.error("Error durante checkout: {}",ex.getMessage(), ex);
            showError("Error al procesar la compra.\n" + ex.getMessage());
        }
    }


}
