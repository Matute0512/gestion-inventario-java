package io.github.torres;

import io.github.torres.dao.ProductDAO;
import io.github.torres.model.Product;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point fot the Inventory Management System.
 * Implements an interactive console-based UI.
 */
public class App {
    public static void main(String[] args) {

        // 1. Initialize Scanner for keyboard input and DAO for database access.
        Scanner scanner = new Scanner(System.in);
        ProductDAO productDao = new ProductDAO();
        boolean isRunning = true;

        // 2. Start the main application loop
        while (isRunning) {
            System.out.println("\n=== SISTEMA DE INVENTARIO ===");
            System.out.println("1. Ver todos los produtos");
            System.out.println("2. Agregar un producto nuevo");
            System.out.println("3. Actualizar un producto");
            System.out.println("4. Eliminar un producto");
            System.out.println("5. Salir");
            System.out.println("Elige una opción: ");

            String option = scanner.nextLine();

            // 3. Handle user choice using a switch statment
            switch (option) {
                case "1":
                    // 3.1 Featch all product and display them
                    System.out.println("\n--- Listado de Productos ---");
                    List<Product> products = productDao.getAll();
                    for (Product p : products) {
                        System.out.println("ID:" + p.getId() + "| Nombre: " + p.getName() +
                                "| Precio: $" + p.getDescription() + "| Stock: " + p.getPrice());
                    }
                    break;

                case "2":
                    // 3.2.1 Request product details from the user
                    System.out.print("Ingrese el nombre: ");
                    String name = scanner.nextLine();
                    System.out.print("Ingrese una descripcion: ");
                    String description = scanner.nextLine();
                    System.out.print("Ingrese el precio del producto: ");
                    Double price = Double.parseDouble(scanner.nextLine());
                    System.out.print("Ingrese el stock del producto: ");
                    Integer stock = Integer.parseInt(scanner.nextLine());

                    // 3.2.2 Instantiate a new product and set its attributes
                    Product product = new Product();
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setStock(stock);

                    // 3.2.3 Save the product to the database using the DAO
                    productDao.save(product);
                    break;
                case "3":
                    System.out.println("\n--- Actualizar Producto ---");

                    // 3.3.1 Request the ID of the product to update
                    System.out.print("Ingrese el ID del producto: ");
                    Integer id = Integer.parseInt(scanner.nextLine());

                    // 3.3.2 Request the new details (name,descripcion, price, stock)
                    System.out.print("Ingrese el nombre: ");
                    String newName = scanner.nextLine();
                    System.out.print("Ingrese una descripcion: ");
                    String newDescription = scanner.nextLine();
                    System.out.print("Ingrese el precio del producto: ");
                    Double newPrice = Double.parseDouble(scanner.nextLine());
                    System.out.print("Ingrese el stock del producto: ");
                    Integer newStock = Integer.parseInt(scanner.nextLine());

                    // 3.3.3 Create a Product object with the apdated data AND the original ID
                    Product newProduct = new Product();
                    newProduct.setId(id);
                    newProduct.setName(newName);
                    newProduct.setDescription(newDescription);
                    newProduct.setPrice(newPrice);
                    newProduct.setStock(newStock);

                    // 3.3.4 Call the DAO to update the product
                    productDao.update(newProduct);

                    break;
                case "4":
                    System.out.println("\n--- Eliminar el Producto ---");

                    // 3.4.1 Request the ID of the product to delete
                    System.out.print("Ingrese el ID a eliminar: ");
                    Integer deleteId = Integer.parseInt(scanner.nextLine());

                    // 3.4.2 Call the DAO to execute the deletion
                    productDao.delete(deleteId);
                    break;
                default:
                    // Handle ivalid inputs
                    System.out.println("\nERROR: Opcion no válida. Por favor, intente nuevamente.");

            }
        }
        scanner.close();
        System.out.println("--- Fin de la operación ---");
    }
}
