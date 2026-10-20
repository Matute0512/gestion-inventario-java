package io.github.torres.service;

import io.github.torres.exception.ValidationException;
import io.github.torres.model.Product;
import java.util.List;

/**
 * Service layer for Product-related business logic.
 *
 * <p>
 *     This interface defines the contract for all product-related operations.
 *     Its acts as a boundary between the controller (UI) and the repository (persistence).
 * </p>
 *
 * <p>
 *     Responsabilities:
 *     <ul>
 *         <li>Business logic and validation</li>
 *         <li>Orchestration of repository operations</li>
 *         <li>Transaction management (if needed)</li>
 *         <li>Logging and monitoring</li>
 *     </ul>
 * </p>
 *
 * @author Matias
 * @version 1.0
 */
public interface ProductService {

    /**
     * Adds a new product to the system.
     *
     * <p>
     *     Process:
     *     <ol>
     *         <li>Validate product data (name, price, stock, description)</li>
     *         <li>Converts DTO to domain model</li>
     *         <li>Logs the operation</li>
     *     </ol>
     * </p>
     *
     * @param product the product to add
     * @throws ValidationException if any field is invalid
     * @throws RuntimeException if database operation fails
     */
    void addProduct(Product product) throws ValidationException;

    /**
     * Updates an existing product.
     *
     * <p>
     * Process:
     * <ol>
     *   <li>Validates that product exists</li>
     *   <li>Validates new data</li>
     *   <li>Updates via repository</li>
     *   <li>Logs the operation</li>
     * </ol>
     * </p>
     *
     * @param product the product with updated data (id must be set)
     * @throws ValidationException if any field is invalid
     * @throws RuntimeException if database operation fails or product not found
     */
    void updateProduct(Product product) throws ValidationException;

    /**
     * Deletes a product by ID.
     *
     * <p>
     * Important: This is a hard delete. Consider implementing soft deletes
     * (marking as deleted_at) for audit purposes in production systems.
     * </p>
     *
     * @param id the product ID to delete
     * @throws RuntimeException if product not found or database operation fails
     */
    void deleteProduct(Integer id);

    /**
     * Retrieves all products from the system.
     *
     * @return a list of all products (empty if no products exist)
     * @throws RuntimeException if database operation fails
     */
    List<Product> getAllProducts();

    /**
     * Searches for products by keyword in name or description.
     *
     * @param keyword the search term (partial match)
     * @return list of matching products (empty if no matches)
     * @throws RuntimeException if database operation fails
     */
    List<Product> searchProducts(String keyword);

    /**
     * Retrieves products sorted by price.
     *
     * @param ascending true for lowest-to-highest, false for highest-to-lowest
     * @return sorted list of products
     * @throws RuntimeException if database operation fails
     */
    List<Product> getSortedByPrice(boolean ascending);

    /**
     * Retrieves all products that are out of stock.
     *
     * @return list of products with stock = 0
     * @throws RuntimeException if database operation fails
     */
    List<Product> getOutOfStockProducts();
}

