package io.github.torres.model;

import java.util.Objects;
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
    
    /**
     * Full constructor used when reading rows from the database.
     * 
     * @param id            database primary key
     * @param name          product name
     * @param description   product description
     * @param price         unit price
     * @param stock         available quantity
     */
    public Product(Integer id, String name, String description, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    /** No-arg constructor required by the DAO when mapping ResultSet rows. */
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

    @Override
    public String toString(){
        return "Producto {id= "+id+", name= "+name+", precio= $"+price+", stock= "+stock+"}";
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return Objects.equals(id,p.id);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(id);
    }

}
