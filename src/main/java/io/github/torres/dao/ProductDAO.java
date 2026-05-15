package io.github.torres.dao;

import io.github.torres.model.Product;
import io.github.torres.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object for the Product Unity.
 * Handles all CRUD (Create, Read, Update, Delete) operations white the databes.
 */
public class ProductDAO {

    /**
     * Inserts a new product into the database.
     * * @param product The product object containing the data to be saved.
     */
    public void save(Product product) {
        // The SQL query with placeholders (?) to prevente SQL Injection
        String sql = "INSERT INTO products(name, description,price, stock) VALUES (?, ?, ?, ?)";

        // We use try-with-resources to open the connection and prepare the statement
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // 1. Inject the actual data in the placeholders (?)
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStock());

            // 2. Execute the command in the database
            int rowsAffected = statement.executeUpdate();

            // 3. Confirm the action
            if (rowsAffected > 0) {
                System.out.println("ÉXITO: ¡Producto guardado correctamente!");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Error al guardar el producto en la base de datos. ");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all products from the database.
     * 
     * @return A list of Product objects.
     */
    public java.util.List<Product> getAll() {
        // Create an empty list to store the products we find
        java.util.List<Product> productList = new java.util.ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                java.sql.ResultSet resultSet = statement.executeQuery()) {

            // Loop through each row in the database result
            while (resultSet.next()) {
                // 1. Create an empty mold for this specific row
                Product product = new Product();

                // 2. Extrct data from the row and put in the object
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));

                // 3. Add the complete product to our list
                productList.add(product);

            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudieron obtener los productos de la base de datos.");
            e.printStackTrace();
        }
        return productList;
    }

    /**
     * Deletes a product from the database using its ID.
     * 
     * @param id The unique identifier of the product to delete.
     */
    public void delete(Integer id) {
        String sql = "DELETE FROM products WHERE id = ? ";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // 1. Replace the ? placeholder with the actual product ID
            statement.setInt(1, id);

            // 2. Execute the DELETE command an store how many rows where rowsAffected
            int numDelete = statement.executeUpdate();

            // 3. If at least one row was deleted, the opperation was succesfull
            if (numDelete > 0) {
                System.out.println("ÉXITO: Se elimino correctamente el producto.");
            }

        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo eliminar el producto.");
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing product in the database.
     * *@param product The product object containing the update data and its
     * original ID.
     */
    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // 1. Inject the updated data into the placeholders (1 to 4)
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStock());

            // 2. Inject the id into the last placeholder (5) for the WHERE clause
            statement.setInt(5, product.getId());

            // 3. Execute the apdate command
            int rowsAffected = statement.executeUpdate();

            // 4. Confirm the action checking if rowsAffected > 0
            if (rowsAffected > 0) {
                System.out.println("ÉXITO: Se actualizo correctamente el producto.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: No se pudo actualizar el producto.");
            e.printStackTrace();
        }
    }

}
