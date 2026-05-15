package io.github.torres.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the database.
 * Provides a centralized point to get the database connection.
 */
public class DatabaseConnection {
    /**
     * The connection URL format is: jdbc:mysql://host:port/database_name
     */
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_inventario";

    /**
     * Database credentials.
     */
    private static final String USER = "root";

    private static final String PASSWORD = "root";

    /**
     * Establishes and returns a connection to the database.
     * 
     * @return A connection object to interact with the database.
     * @throws SQLException If a database access error occurs or the url is null.
     */
    public static Connection getConnection() throws SQLException {
        // The DriverManager acts as the bridge between Java and mysql
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
