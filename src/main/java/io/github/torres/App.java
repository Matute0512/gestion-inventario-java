package io.github.torres;

import io.github.torres.dao.ProductDAO;
import io.github.torres.model.Product;
import java.util.List;

/**
 * Main entry point to the test the system´s infrastructure.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("--- Iniciando Sistema de Actualización ---");

        ProductDAO productDao = new ProductDAO();

        // 1. Creamos un producto nuevo PERO le seteamos el ID original (1)
        Product updateProduct = new Product();
        updateProduct.setId(1);
        updateProduct.setName("Monitor Samsung 24 pulgadas - EDICION GAMER");
        updateProduct.setDescription("Monitor LED Full HD 144Hz");
        updateProduct.setPrice(210.50);
        updateProduct.setStock(10);

        // 2. Le pedimos al DAO que lo actualice
        System.out.println("Actualizando datos del producto..");
        productDao.update(updateProduct);

        System.out.println("--- Fin de la operación ---");
    }
}
