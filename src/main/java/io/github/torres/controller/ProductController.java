package io.github.torres.controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import io.github.torres.dao.ProductDAO;
import io.github.torres.dao.ProductDAO.DAOException;
import io.github.torres.model.Product;
import io.github.torres.view.MainView;

/**
 * Orchestrates the data flow between the {@link MainView} (GUI) and the
 * {@link ProductDAO} (database).
 *
 * <p>
 * Represents the CONTROLLER layer in the MVC pattern.
 * All Swing interactions are performed on the Event Dispatch Thread.
 * </p>
 */
public class ProductController {

    private final MainView view;
    private final ProductDAO productDAO;

    // Variable to remember which ID we are currently editing
    private Integer currentEditingId = null;

    /**
     * Wires the view and DAO together and performs an initial data load.
     *
     * @param view       the main application window.
     * @param productDAO the data-access object for products.
     */
    public ProductController(MainView view, ProductDAO productDAO) {
        this.view = view;
        this.productDAO = productDAO;

        // Attach event listeners to the GUI Buttons
        this.view.getBtnAdd().addActionListener(e -> addProduct());
        this.view.getBtnDelete().addActionListener(e -> deleteProduct());
        this.view.getBtnUpdate().addActionListener(e -> updateProduct());

        // Listener to detect when a row in the table is clicked
        this.view.getTblProducts().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadProductIntoForm();
            }
        });

        // Initial load of data when the application starts
        refreshTable();
    }

    /**
     * Fetches all products from MySQL and updates the JTable view.
     */
    private void refreshTable() {
        try {
            List<Product> products = productDAO.getAll();

            // Table headers matching our view columns
            String[] columns = { "ID", "Nombre", "Precio", "Stock", "Descripción" };

            // DefaultTableModel allows us to dynamically alter rows
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Populate the data matrix
            for (Product p : products) {
                Object[] row = { p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getDescription() };
                model.addRow(row);
            }

            view.getTblProducts().setModel(model);
        } catch (DAOException ex) {
            showError("No se pudo cargar la lista de productos.\n" + ex.getMessage());
        }
    }

    /**
     * Populates the forms fields with data from the currently selected product
     * in the table, enabling edit mode for updating an existing record.
     */
    private void loadProductIntoForm() {
        JTable table = view.getTblProducts();
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            currentEditingId = (int) table.getValueAt(selectedRow, 0);

            // We fill the text boxes with the data from the row
            view.setTextName(table.getValueAt(selectedRow, 1).toString());
            view.setTextPrice(table.getValueAt(selectedRow, 2).toString());
            view.setTextStock(table.getValueAt(selectedRow, 3).toString());
            view.setTextDescription(table.getValueAt(selectedRow, 4).toString());

            // We turn off Add and turn on Save Changes
            view.getBtnAdd().setEnabled(false);
            view.getBtnUpdate().setEnabled(true);
        }
    }

    /**
     * Gathers inputs from the form, validates them, and saves a new product to the
     * databese.
     */
    private void addProduct() {
        String name = view.getTextName();
        String description = view.getTextDescription();
        String priceStr = view.getTextPrice();
        String stockStr = view.getTextStock();

        // Basic Defensive Validation
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Por favor, rellene todos los campos obligatorios (Nombre, Precio, Stock)", "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double price;
        Integer stock;

        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "Formato de datos no válido. El precio debe ser decimal y el stock un número entero.",
                    "Error de formato de datos",
                    JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(
                    view,
                    "Producto agregado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            view.clearFields();
            view.getTblProducts().clearSelection();
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

        String name = view.getTextName();
        String description = view.getTextDescription();
        String priceStr = view.getTextPrice();
        String stockStr = view.getTextStock();

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
            view.clearFields();
            currentEditingId = null;
            view.getBtnAdd().setEnabled(true);
            view.getBtnUpdate().setEnabled(false);
            view.getTblProducts().clearSelection();

            refreshTable();
        } catch (NumberFormatException ex) {
            showError("Formato númerico inválido");
        } catch (DAOException ex) {
            showError("No se puede actualizar el producto.\n" + ex.getMessage());
        }
    }

    /**
     * Identifies the selected row in the table and removes it from the database.
     */
    private void deleteProduct() {
        JTable table = view.getTblProducts();
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    view,
                    "Seleccione un producto de la tabla a eliminar.",
                    "Error de Selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extract the value from the ID column (Column index 0)
        int id = (int) (table.getValueAt(selectedRow, 0));

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro que desea eliminar este producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productDAO.delete(id);
                JOptionPane.showMessageDialog(
                        view,
                        "Producto eliminado correctamente.",
                        " Eliminado",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } catch (DAOException ex) {
                showError("No se pudo eliminar el producto.\n" + ex.getMessage());
            }
        }
    }

    /**
     * Displays a modal error dialog with the given message.
     *
     * @param message the localised (Spanish) error description shown to the user.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                view,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
