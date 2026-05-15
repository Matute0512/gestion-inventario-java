-- We create the database if it doesn´t exist and put it into use
CREATE DATABASE IF NOT EXISTS gestion_inventario;
USE gestion_inventario;

-- We Created the 'products' table
CREATE TABLE IF NOT EXISTS products(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);