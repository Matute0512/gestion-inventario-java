package io.github.torres;

import io.github.torres.dao.ProductDAO;
import io.github.torres.model.Product;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Inventory Management System.
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
            System.out.println("1. Ver todos los productos");
            System.out.println("2. Agregar un producto nuevo");
            System.out.println("3. Actualizar un producto");
            System.out.println("4. Eliminar un producto");
            System.out.println("5. Salir");
            System.out.print("Elige una opción: ");

            String option = scanner.nextLine();

            // 3. Handle user choice using a switch statment
            switch (option) {
                case "1":
                    // 3.1 Featch all product and display them
                    System.out.println("\n--- Listado de Productos ---");
                    List<Product> products = productDao.getAll();
                    if (!products.isEmpty()) {
                        for (Product p : products) {
                            System.out.println("ID: " + p.getId()
                                    + " | Nombre: " + p.getName()
                                    + " | Descripción: " + p.getDescription()
                                    + " | Precio: $" + p.getPrice()
                                    + " | Stock: " + p.getStock());
                        }
                    } else {
                        System.out.println("No hay Productos disponibles");
                    }
                    break;

                case "2":
                    // 3.2.1 Request product details from the user
                    String name = readString(scanner, "Ingrese el nombre: ");

                    String description = readString(scanner, "Ingrese la descripcion del producto: ");

                    Double price = readDouble(scanner, "Ingrese el precio del producto: ");

                    Integer stock = readInt(scanner, "Ingrese el stock del producto: ");

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

                    Integer id = readInt(scanner, "Ingrese el ID del producto: ");

                    // 3.3.2 Request the new details (name,descripcion, price, stock)
                    String newName = readString(scanner, "Ingrese el nombre: ");
                    String newDescription = readString(scanner, "Ingrese una descripcion: ");
                    Double newPrice = readDouble(scanner, "Ingrese el precio del producto: ");
                    Integer newStock = readInt(scanner, "Ingrese el stock del producto: ");

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
                    Integer deleteId = readInt(scanner, "Ingrese el ID a eliminar: ");

                    // 3.4.2 Call the DAO to execute the deletion
                    productDao.delete(deleteId);
                    break;
                case "5":
                    isRunning = false;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    // Handle ivalid inputs
                    System.out.println("\nERROR: Opcion no válida. Por favor, intente nuevamente.");

            }
        }
        scanner.close();
        System.out.println("--- Fin de la operación ---");
    }

    /**
     * Reads and validates an integer value entered by the user.
     * Repeats the prompt until a valid integer is provided.
     * 
     * @param scanner Scanner used to read user input.
     * @param message Message displayed to request input.
     * @return A valid integer entered by te user
     */
    private static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Debe ingresar un número entero válido.");
            }
        }
    }

    /**
     * Reads and validates a decimal number entered by the user.
     * Repeats the prompt until a valid number value is provided.
     * 
     * @param scanner Scanner used to read user input.
     * @param message Message displayed to request input.
     * @return A valid double entered by the user.
     */
    private static double readDouble(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Debe ingresar un número válido.");
            }
        }
    }

    /**
     * Reads and validates a non-empty text string entered by the user.
     * Removes leading and trailing spaces, and repeats the prompt
     * until the user provides valid text.
     *
     * @param scanner Scanner used to read user input.
     * @param message Message displayed to request input.
     * @return A valid non-empty string entered by the user.
     */
    private static String readString(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("ERROR: El campo no puede estar vacío.");
        }
    }
}
