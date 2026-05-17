package io.github.torres.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
     * Establishes and returns a new JDBC connection to the database.
     *
     * <p>
     * The caller is responsible for closing the connection (use
     * try-with-resources).
     * </p>
     *
     * @return a fresh {@link Connection} to the configured database.
     * @throws SQLException if the connection cannot be established.
     */
    public static Connection getConnection() throws SQLException {
        // The DriverManager acts as the bridge between Java and mysql
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        // Fail fast if the DB is unreachable (5 seconds)
        props.setProperty("connectTimeout", "5000");
        return DriverManager.getConnection(URL, props);
    }
}
