package io.github.torres.util;

import io.github.torres.exception.ValidationException;
import io.github.torres.view.styles.Theme;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for common validation operations.
 *
 * Provides static methods to validate user input and data integrity.
 * All validation failures throw {@link ValidationException} with user-friendly messages.
 *
 * @author Matias
 * @version 1.0
 */
public class ValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);

    /**
     * Validates that all required fields are not empty.
     *
     * @param fields variable number field values to validate
     * @param fieldNames variable number of field names for error messages
     * @throws ValidationException if any field is empty or null
     */
    public static void validateNotEmpty(String[] fields, String[] fieldNames) throws ValidationException {
        if (fields.length != fieldNames.length) {
            throw new ValidationException("❌ Error interno: número de campos no coincide");
        }

        for(int i = 0; i< fields.length; i++) {
            if(fields[i] == null || fields[i].trim().isEmpty()) {
                LOGGER.warn("❌ Campo vacío encontrado: {}",fields[i]);
                throw  new ValidationException("❌ El campo '" + fieldNames[i] + "' es obligatorio");
            }
        }
    }

    /**
     * Validates product name length.
     *
     * @param name the product name to validate.
     * @throws ValidationException if name exceeds maximum length.
     */
    public static void validateProductName(String name) throws ValidationException {
        if(name == null || name.trim().isEmpty()){
            throw new ValidationException("❌ El nombre del producto es obligatorio");
        }

        if (name.length() > Theme.MAX_PRODUCT_NAME_LENGTH){
            LOGGER.warn("Nombre de producto demasiado largo: {} caracteres", name.length());
            throw  new ValidationException(
                    "❌ El nombre del producto no puede exceder " + Theme.MAX_PRODUCT_NAME_LENGTH + " caracteres"+
                            "Longitud actual: "+ name.length());
        }
    }

    /**
     * Validates product description length.
     *
     * @param description the product description to validate
     * @throws ValidationException if description exceeds maximum length
     */
    public static void validateProductDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()){
            description = "";
        }

        if (description.length() > Theme.MAX_DESCRIPTION_LENGTH){
            LOGGER.warn("Descripcion demasiado largo: {} caracteres", description.length());
            throw new ValidationException("❌ La descripción no puede exceder " + Theme.MAX_DESCRIPTION_LENGTH + " caracteres. " +
                    "Longitud actual: " + description.length());
        }
    }

    /**
     * Parses and validates a price string
     *
     * @param priceStr the price as a string
     * @return the parsed double price value
     * @throws ValidationException if format is invalid or price is out of range
     */
    public static Double validatePrice(String priceStr) throws ValidationException {
        if (priceStr == null || priceStr.trim().isEmpty()){
            throw new ValidationException("❌ El precio es obligatorio");
        }
        Double price;
        try{
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            LOGGER.warn("Formato de precio inválido: {}", priceStr);
            throw new ValidationException("❌ El precio debe ser un número decimal válido (ej: 19.99)");
        }
        if (price < Theme.MIN_PRICE || price > Theme.MAX_PRICE) {
            LOGGER.warn("Precio fuera de rango: {}", priceStr);
            throw new ValidationException(
                    "❌ El precio debe estar entre $" + String.format("%.2f", Theme.MIN_PRICE) +
                            " y $" + String.format("%.2f", Theme.MAX_PRICE));
        }

        return price;
    }

    /**
     * Parses and validates a stock string.
     *
     * @param stockStr the stock as a string
     * @return the parsed integer stock value
     * @throws ValidationException if format is invalid or stock is negative
     */
    public static Integer validateStock (String stockStr) throws ValidationException {
        if (stockStr == null || stockStr.trim().isEmpty()){
            throw new ValidationException("❌ El stock es obligatorio");
        }

        Integer stock;
        try{
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            LOGGER.warn("Formato de stock inválido: {}", stockStr);
            throw new ValidationException("❌ El stock debe ser un número entero válido (ej: 100)");
        }
        if (stock < 0 || stock > Theme.MAX_STOCK) {
            LOGGER.warn("Stock fuera de rango: {}", stock);
            throw new ValidationException("❌ El stock debe ser un número entero entre 0 y " + Theme.MAX_STOCK);
        }
        return stock;
    }

    /**
     * Validates all product fields together
     *
     * @param name the product name
     * @param description the product description
     * @param priceStr the product price as a string
     * @param stockStr the product stock as a string
     * @return a double array [price, stock] if all validation pass
     * @throws ValidationException if any validation fails
     */
    public static double[] validateProductFields(String name, String description, String priceStr, String stockStr) throws ValidationException {
        LOGGER.debug("Validando campos de producto");

        // Validate individual fields
        validateProductName(name);
        validateProductDescription(description);
        Double price = validatePrice(priceStr);
        Integer stock = validateStock(stockStr);

        LOGGER.debug("✅ Validación de campos completada exitosamente");

        return  new double[]{price,stock};
    }

    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private ValidationUtil() {
        throw  new AssertionError("ValidationUtil no puede ser instanciado");
    }
}
