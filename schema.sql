-- We create the database if it doesn't exist and put it into use
CREATE DATABASE IF NOT EXISTS gestion_inventario;
USE gestion_inventario;
-- We Created the 'products' table
CREATE TABLE IF NOT EXISTS products(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- CONSTRAINTS DE NEGOCIO
    CONSTRAINTS check_price CHECK (price > 0),
    CONSTRAINTS check_stock CHECK (stock >= 0),
    -- INDICES PARA PERFORMANCE
    INDEX idx_name (name),
    INDEX idx_stock (stock)
);
-- 1. Main sales table (The Ticket):
CREATE TABLE IF NOT EXISTS sales(
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'COMPLETED',
    CONSTRAINTS check_amount CHECK (total_amount > 0),
    INDEX idx_sale_date (sale_date),
    INDEX idx_status (status)
);
-- 2. Details table (The lines on the ticket):
CREATE TABLE IF NOT EXISTS sale_details(
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price INT NOT NULL CHECK (unit_price > 0),
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_sale_id(sale_id),
    INDEX idx_product_id (product_id)
);
CREATE TABLE IF NOT EXISTS audit_log(
    id INT AUTO_INCREMENT PRIMARY KEY,
    operation VARCHAR(20),
    tabla_name VARCHAR(50),
    record id INT,
    old_value JSON,
    new_value JSON,
    timestamp DATATIME DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(50)
);
