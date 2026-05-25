package io.github.torres.model;

/**
 * Data Transfer Object (DTO) to represent an item within the shopping cart.
 * This class encapsulates the details of a product added to the cart, including its identifier,
 */
public class CartItem {
    private int productId;
    private int quantity;
    private double subtotal;

    public CartItem() {
    }

    public CartItem(int productId, int quantity, double subtotal) {
        this.productId = productId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

}
