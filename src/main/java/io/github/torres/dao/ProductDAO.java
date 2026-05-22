package io.github.torres.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import io.github.torres.config.DatabaseConnection;
import io.github.torres.model.Product;

/**
 * Data Access Object for the {@link Product} entity. Handles all CRUD operations against the
 * {@code products} table using JDBC.
 *
 * <p>
 * All methods propagate {@link SQLException} as a checked {@link DAOException} (unchecked wrapper)
 * so callers (Controller) can display meaningful error dialogs without leaking JDBC types into the
 * UI.
 * </p>
 */
public class ProductDAO {

    // SQL constants
    private static final String SQL_INSERT =
            "INSERT INTO products(name, description,price, stock) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL = "SELECT * FROM products";

    private static final String SQL_DELETE = "DELETE FROM products WHERE id = ? ";

    private static final String SQL_UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE id = ?";

    private static final String SQL_SEARCH =
            "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";

    /**
     * Inserts a new product into the database. * @param product The product object containing the
     * data to be saved.
     *
     * @throws DAOException if a database error occurs.
     */
    public void save(Product product) {

        // We use try-with-resources to open the connection and prepare the statement
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {

            // 1. Inject the actual data in the placeholders (?)
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStock());

            // 2. Execute the command in the database
            int rowsAffected = statement.executeUpdate();

            // 3. Confirm the action
            if (rowsAffected > 0) {
                System.out.println("ÉXITO: Producto guardado correctamente - " + product.getName());
            }
        } catch (SQLException e) {
            throw new DAOException("ERROR al guardar el producto - " + product.getName(), e);

        }
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of Product objects.
     * @throws DAOException if a database error occurs
     */
    public List<Product> getAll() {
        // Create an empty list to store the products we find
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                java.sql.ResultSet resultSet = statement.executeQuery()) {

            // Loop through each row in the database result
            while (resultSet.next()) {
                // 1. Create an empty mold for this specific row
                Product product = mapResultSetProduct(resultSet);

                // 2. Add the complete product to our list
                productList.add(product);

            }
        } catch (SQLException e) {
            throw new DAOException(
                    "ERROR: No se pudieron obtener los productos de la base de datos.", e);

        }
        return productList;
    }

    /**
     * Deletes a product from the database using its ID.
     *
     * @param id The unique identifier of the product to delete.
     * @return {@code true} if at least one row was a deleted.
     * @throws DAOException if a database error occurs.
     */
    public boolean delete(Integer id) {

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            // 1. Replace the ? placeholder with the actual product ID
            statement.setInt(1, id);

            // 2. Execute the DELETE command an store how many rows where rowsAffected
            int rowsDeleted = statement.executeUpdate();

            // 3. If at least one row was deleted, the opperation was succesfull
            if (rowsDeleted > 0) {
                System.out.println("ÉXITO: Se elimino correctamente el producto con id= " + id);
            }
            return rowsDeleted > 0;

        } catch (SQLException e) {
            throw new DAOException("ERROR: No se pudo eliminar el producto id= " + id, e);

        }
    }

    /**
     * Updates an existing product in the database.
     *
     * @param product The product object containing the update data and its
     * @return {@code true} if the row was found and updated.
     * @throws DAOException if a database error occurs.
     */
    public boolean update(Product product) {

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

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
                System.out.println(
                        "ÉXITO: Se actualizo correctamente el producto id= " + product.getId());
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("ERROR no se pudo actualizar el producto: " + product.getId(),
                    e);

        }
    }

    /**
     * Searches products by name or description.
     *
     * @param keyword The search keyword.
     * @return A list of matching products.
     * @throws DAOException if a database error occurs.
     */
    public List<Product> search(String keyword) {
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_SEARCH)) {

            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapResultSetProduct(resultSet);
                    productList.add(product);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(
                    "Error: No se puede realizar la busqueda con la palabra: " + keyword, e);
        }
        return productList;
    }

    /**
     * Sorts all products by price.
     *
     * @param ascending If true, sorts from lowest to highest. If false, highest to lowest.
     * @return A sorted list of products.
     * @throws DAOException if a database error occurs.
     */
    public List<Product> sortByPrice(boolean ascending) {
        List<Product> productList = new ArrayList<>();

        // In SQL, we cannot parametrize ASC/DESC with "?", so we build the query
        String order = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM products ORDER BY price " + order;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Product product = mapResultSetProduct(resultSet);
                productList.add(product);
            }
        } catch (SQLException e) {
            throw new DAOException("Error: No se puede ordenar los productos por precio", e);
        }
        return productList;
    }

    /**
     * Retrieves all products that currently have zero stock.
     *
     * @return A list of out-of-stock products.
     * @throws DAOException if a database error occurs.
     */
    public List<Product> getOutOfStock() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock = 0";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = mapResultSetProduct(resultSet);
                productList.add(product);
            }
        } catch (SQLException e) {
            throw new DAOException("Error: No se pudieron filtrar los productos agotados.", e);
        }
        return productList;
    }

    /**
     * Maps the current row of a ResultSet into a Product object.
     *
     * @param resultSet The ResultSet positioned on a valid row.
     * @return A populated Product object.
     * @throws SQLException if column extraction fails.
     */
    private Product mapResultSetProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();

        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getDouble("price"));
        product.setStock(resultSet.getInt("stock"));

        return product;
    }

    /**
     * Reduces the stock of a specific product by a given quantity. Uses a direc SQL UPDATE to
     * ensure atomic database operations.
     * 
     * @param productId the ID of the product sold
     * @param quantity the mount to substract from current stock
     * @throws DAOExeption if the database operation fails
     */
    public void reduceStock(int productId, int quantity) throws DAOException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, productId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el stock durante el proceso de pago: ", e);
        }
    }

    /**
     * Unchecked wrapper for {@link SQLException} thrown by this DAO. Prevents JDBC types from
     * leaking into the controller or view layers.
     */
    public static class DAOException extends RuntimeException {

        /**
         * Creates a new DAOException.
         *
         * @param message Detailed error message.
         * @param cause Original SQLException cause.
         */
        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
