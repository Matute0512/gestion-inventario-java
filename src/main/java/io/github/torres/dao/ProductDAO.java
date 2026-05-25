package io.github.torres.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.torres.config.DatabaseConnection;
import io.github.torres.model.CartItem;
import io.github.torres.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);
    // SQL constants
    private static final String SQL_INSERT =
            "INSERT INTO products(name, description,price, stock) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL = "SELECT * FROM products";

    private static final String SQL_DELETE = "DELETE FROM products WHERE id = ? ";

    private static final String SQL_UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE id = ?";

    private static final String SQL_SEARCH =
            "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";

    private static final String SQL_OUT_STOCK = "SELECT * FROM products WHERE stock = 0";

    private static final String SQL_REDUCE_STOCK =
            "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";

    private static final String SQL_INSERT_SALE = "INSERT INTO sales (total_amount) VALUES (?)";

    private static final String SQL_INSERT_DETAIL =
            "INSERT INTO sale_details (sale_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)";


    /**
     * Inserts a new product into the database.
     *
     * @param product The product object containing the data to be saved.
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
                logger.info("Producto guardado correctamente: {}", product.getName());
            }
        } catch (SQLException e) {
            logger.error("Error al guardar producto: {}",product.getName(), e);
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
                Product product = mapResultSetToProduct(resultSet);

                // 2. Add the complete product to our list
                productList.add(product);

            }
        } catch (SQLException e) {
            logger.error("Error al traer todos los productos: {}",e.getMessage(), e);
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
                logger.info("Producto eliminado correctamente. Id: {}", id);
            }
            return rowsDeleted > 0;

        } catch (SQLException e) {
            logger.error("Error al eliminar el producto: {}", id, e);
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
                logger.info("Producto actualizado correctamente. ID: {}",product.getId());
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error al actualizar el producto: {}", product.getName(), e);
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
                    Product product = mapResultSetToProduct(resultSet);
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
                Product product = mapResultSetToProduct(resultSet);
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

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_OUT_STOCK);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
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
    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();

        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getDouble("price"));
        product.setStock(resultSet.getInt("stock"));

        return product;
    }

    /**
     * Reduces the stock of a specific product by a given quantity. Uses a direct SQL UPDATE to
     * ensure atomic database operations.
     *
     * @param productId the ID of the product sold
     * @param quantity the mount to substract from current stock
     * @throws DAOException if the database operation fails
     */
    public void reduceStock(int productId, int quantity) throws DAOException {

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_REDUCE_STOCK)) {

            // Amount to subtract
            statement.setInt(1, quantity);

            // Product id
            statement.setInt(2, productId);

            // Validation: Only updates if stock >= quantity
            statement.setInt(3, quantity);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DAOException("Stock insuficiente para completar la venta.", null);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el stock durante el proceso de pago: ", e);
        }
    }

    /**
     * Registers a complete sale transaction atomically.
     * Inserts the sale header, the sale details, and reduces the stock.
     * If any operation fails (e.g., insufficient stock), it rolls back the entire transaction
     *
     * @param totalAmount The total cost of the sale.
     * @param cartItems A list of arrays containing [productId, quantity, subtotal]
     * @throws DAOException if the transaction fails.
     */
    public void registerSale(double totalAmount, List<CartItem> cartItems) throws DAOException{
        Connection connection = null;

        try{
            // Get connection from pool
            connection = DatabaseConnection.getConnection();

            // Critical: Disable auto-commit to start a manual Transaction
            connection.setAutoCommit(false);

            // 1. Insert the Sale Header and retrieve the auto-generated ID
            int saleId = -1;
            try(PreparedStatement statementSale = connection.prepareStatement(SQL_INSERT_SALE,java.sql.Statement.RETURN_GENERATED_KEYS)){

                statementSale.setDouble(1, totalAmount);
                statementSale.executeUpdate();

                // Retrieve the auto-generated sale ID
                try (ResultSet generatedKeys = statementSale.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        saleId = generatedKeys.getInt(1);
                    } else{
                        throw new SQLException("No se pudo obtener el ID de la venta generada.");
                    }
                }
            }

            // 2. Insert Sale Details and 3. Update stock using Batch Processing
            try(PreparedStatement statementDetail = connection.prepareStatement(SQL_INSERT_DETAIL);
                PreparedStatement statementStock = connection.prepareStatement(SQL_REDUCE_STOCK)){

                for(CartItem item: cartItems){
                    int productId = item.getProductId();
                    int quantity = item.getQuantity();
                    double subtotal = item.getSubtotal();

                    logger.debug("Procesando producto ID: {}, cantidad: {}",productId,quantity);

                    // Prepare the Detail insertion
                    statementDetail.setInt(1, saleId);
                    statementDetail.setInt(2, productId);
                    statementDetail.setInt(3,quantity);
                    statementDetail.setDouble(4, subtotal);
                    statementDetail.addBatch();

                    // ========== Stock Reduction ==========
                    // SQL_REDUCE_STOCK: "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?"
                    // Parameter 1: amount to subtract
                    statementStock.setInt(1,quantity);
                    // Parameter 2: Product ID
                    statementStock.setInt(2,productId);
                    // Parameter 3:
                    statementStock.setInt(3,quantity);
                    statementStock.addBatch();
                }
                // Execute all queued statements
                int [] detailInserts = statementDetail.executeBatch();
                int[] stockUpdates = statementStock.executeBatch();

                // Validate that all inserts were successful
                for (int insertCount: detailInserts){
                    if(insertCount == 0 || insertCount == Statement.EXECUTE_FAILED){
                        throw new SQLException("Error al insertar detalles de venta");
                    }
                }

                // Validate that all stock updates were successful (i.e., no insufficient stock)
                for (int updateCount: stockUpdates){
                    if (updateCount == 0 || updateCount == Statement.EXECUTE_FAILED){
                        throw new SQLException("Stock insuficiente en uno o más productos.");
                    }
                }

                logger.debug("Se procesaron {} detalles de venta", detailInserts.length);
            }

            // ============================================
            // Commit Transaction
            // ============================================
            connection.commit();
            logger.info("Transacción de venta completada exitosamente. ID: {}, Monto: ${}",
                    saleId,totalAmount);

        } catch (SQLException sqlException){
            // Rollback transaction on error
            if (connection != null) {
                try{
                    connection.rollback();
                    logger.warn("Transacción revertida por error de SQL");
                } catch(SQLException rollbackException){
                    logger.error("Error al hacer rollback de SQL", rollbackException);
                }
            }
            logger.error("Error durante la transacción de venta: {}", sqlException.getMessage(), sqlException);
            throw  new DAOException("Error al registrar la venta; "+ sqlException.getMessage(), sqlException);
        } catch (Exception unexpectionException){
            // Rollback transaction on unexpected error
            if (connection != null) {
                try{
                    connection.rollback();
                    logger.warn("Transacción revertida por error inesperado");
                } catch(SQLException rollbackException){
                    logger.error("Error al hacer rollback", rollbackException);
                }
            }
            logger.error("Error inesperado durante venta", unexpectionException);
            throw new DAOException("Error inesperado al registrar la venta", unexpectionException);
        } finally {
            // Restore auto-commit mode and close connection
            if (connection != null) {
                try{
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeException){
                    logger.warn("Error al cerrar la conexión",closeException);
                }
            }
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
