package io.github.torres.view;

import javax.swing.*;
import java.awt.*;

/**
 * Main window for the Inventory Management application.
 * Represents the VIEW layer in the MVC pattern.
 *
 * <p>
 * This class is responsible only for building and exposing UI components.
 * All business logic lives in the Controller.
 * </p>
 */
public class MainView extends JFrame {

    // Visual Components (Buttons, Tables, TextField)
    private JTextField textName, textPrice, textStock;
    private JTextArea textDescription;
    private JButton btnAdd, btnDelete, btnUpdate;
    private JTable tblProducts;

    /** Builds and lays out all Swing components. */
    public MainView() {
        // 1. Basic Window Configuration
        setTitle("SGE - Gestión de Inventario");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window on screen

        // 2. Initialize layout and Components
        setLayout(new BorderLayout(10, 10));

        // Initialize and assamble panels
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Creates the form panel for data input (Left side)
     * 
     * @return panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalles del Producto"));
        panel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Nombre:"), gbc);
        textName = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(textName, gbc);

        // Price Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Precio:"), gbc);
        textPrice = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(textPrice, gbc);

        // Stock Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Stock:"), gbc);
        textStock = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(textStock, gbc);

        // Description Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Descripción:"), gbc);
        textDescription = new JTextArea(3, 15);
        textDescription.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(textDescription);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(scrollDesc, gbc);

        // Add Button
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnAdd = new JButton("Agregar");
        btnUpdate = new JButton("Guardar Cambios");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(buttonPanel, gbc);

        // Spacer to push commponents to the tblProducts
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    /**
     * Creats the panel that displays the data grid (Center/Right side)
     * 
     * @return panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Inventario de Stock"));

        // Placeholder table (We will bind this to a proper Model later)
        String[] columns = { "ID", "Nombre", "Precio", "Stock", "Descripción" };
        tblProducts = new JTable(new Object[0][0], columns);
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProducts.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(tblProducts), BorderLayout.CENTER);

        // Control Panel below the table
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDelete = new JButton("Eliminar seleccionados");
        controlPanel.add(btnDelete);
        controlPanel.add(btnDelete);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Getters to allow the Controller to read input data
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

    // Getters for the buttons to bind action listeners in the Controller
    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    // Getter for the table to update its data model
    public JTable getTblProducts() {
        return tblProducts;
    }

    // Setters
    public void setTextName(String text) {
        this.textName.setText(text);;
    }

    public void setTextPrice(String text) {
        this.textPrice.setText(text);;
    }

    public void setTextStock(String text) {
        this.textStock.setText(text);;
    }

    public void setTextDescription(String text) {
        this.textDescription.setText(text);
    }

    /**
     * Clears all text fields after successful operation.
     */
    public void clearFields() {
        textName.setText("");
        textPrice.setText("");
        textStock.setText("");
        textDescription.setText("");
    }

}
