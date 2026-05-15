package io.github.torres.model;

/**
 * Represents a physical product in the inventory system
 * This entity maps to the 'products' table in the database
 */
public class Product {

    // --- Atributs (State) ---

    /**
     * Unique identifier for the product.
     * Mapped to the Primary Key in the database.
     */
    private Integer id;

    /**
     * The commercial name of the product.
     */
    private String name;
    /**
     * A detailed description of the product´s features.
     */
    private String description;
    /**
     * The retail price of the product.
     * Using Double to handle decimals and avoid null-reference issues.
     */
    private Double price;
    /**
     * The current quantity available in the warehouse.
     */
    private Integer stock;
    
    public Product(Integer id, String name, String description, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public Product() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}
