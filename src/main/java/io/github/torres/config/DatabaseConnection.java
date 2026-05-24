package io.github.torres.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database Connection Manager using HikariCP Connection Pool.
 *
 * This class manages the lifecycle of database connections and provides
 * a reusable connection pool for improved performance.
 *
 * Configuration is loaded from: src/main/resources/application.properties
 *
 * @author Matias
 * @version 2.0
 */
public class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    // ===========================================
    // Configuration constants (from properties)
    // ===========================================
    private static final String DATABASE_URL;
    private static final String DATABASE_USER;
    private static final String DATABASE_PASSWORD;
    private static final int CONNECTION_POOL_SIZE;
    private static final int CONNECTION_POOL_MIN_IDLE;
    private static final int CONNECTION_TIMEOUT_MS;

    // ==========================================
    // HikariCP Connection Pool
    // ==========================================
    private static final HikariDataSource dataSource;

    static {
        Properties configProperties = new Properties();

        try (InputStream resourceStream = DatabaseConnection.class.getResourceAsStream("/application.properties")) {
            if (resourceStream == null) {
                String errorMessage = "❌ Archivo de configuración no encontrado: application.properties";
                logger.error(errorMessage);
                throw new IOException(errorMessage);
            }
            configProperties.load(resourceStream);

            // Extract database configuration
            DATABASE_URL = configProperties.getProperty("db.url");
            DATABASE_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : configProperties.getProperty("db.user");
            DATABASE_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : configProperties.getProperty("db.password");

            // Extract connection pool configuration
            CONNECTION_POOL_SIZE = Integer.parseInt(configProperties.getProperty("db.pool.size", "10"));
            CONNECTION_POOL_MIN_IDLE = Integer.parseInt(configProperties.getProperty("db.pool.min.idle", "2"));
            CONNECTION_TIMEOUT_MS = Integer.parseInt(configProperties.getProperty("db.connection.timeout", "5000"));

            if (DATABASE_URL == null || DATABASE_USER == null || DATABASE_PASSWORD == null) {
                String errorMessage = "❌ Configuración de base de datos incompleta. " +
                        "Verifica: db.url, db.user, db.password";
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }

            // Initialize HikariCP connection pool
            dataSource = initializeHikariCP();

            logger.info("✅ Pool de conexiones HikariCP inicializado exitosamente");
            logger.info("   URL: {}", DATABASE_URL);
            logger.info("   Usuario: {}", DATABASE_USER);
            logger.info("   Tamaño del pool: {} (mínimo inactivo: {})", CONNECTION_POOL_SIZE, CONNECTION_POOL_MIN_IDLE);

        } catch (IOException ioException) {
            String errorMessage = "❌ Error al cargar application.properties";
            logger.error(errorMessage, ioException);
            throw new ExceptionInInitializerError(ioException);
        } catch (NumberFormatException numberException) {
            String errorMessage = "❌ Valores numéricos inválidos en configuración";
            logger.error(errorMessage, numberException);
            throw new ExceptionInInitializerError(numberException);
        } catch (Exception unexpectedException) {
            String errorMessage = "❌ Error inesperado al inicializar la conexión a la base de datos";
            logger.error(errorMessage, unexpectedException);
            throw new ExceptionInInitializerError(unexpectedException);
        }
    }

    /**
     * Initializes and configures the HikariCP connection pool.
     *
     * HikariCP is a high-performance JDBC connection pool that:
     * - Reuses connections instead of creating new ones
     * - Automatically manages connection lifecycle
     * - Detects abandoned connections
     * - Provides monitoring capabilities
     *
     * @return Configured HikariDataSource instance ready for use.
     */
    private static HikariDataSource initializeHikariCP() {
        HikariConfig hikariConfig = new HikariConfig();

        // ===========================================
        // Database Connection Properties
        // ===========================================
        hikariConfig.setJdbcUrl(DATABASE_URL);
        hikariConfig.setUsername(DATABASE_USER);
        hikariConfig.setPassword(DATABASE_PASSWORD);

        // ===========================================
        // Connection Pool Configuration
        // ===========================================
        // Maximum number of connections in the pool (default: 10)
        hikariConfig.setMaximumPoolSize(CONNECTION_POOL_SIZE);

        // Minimum number of idle connections to maintain (default: 2)
        hikariConfig.setMinimumIdle(CONNECTION_POOL_MIN_IDLE);

        // Maximum time (milliseconds) to wait for a connection from the pool
        hikariConfig.setConnectionTimeout(CONNECTION_TIMEOUT_MS);

        // Maximum time (milliseconds) a connection can remain idle before being closed
        hikariConfig.setIdleTimeout(600000); // 10 minutes

        // Maximum time (milliseconds) a connection can be active
        hikariConfig.setMaxLifetime(1800000); // 30 minutes

        // Enable automatic connection leak detection
        hikariConfig.setLeakDetectionThreshold(60000); // 1 minute

        // Connection pool name for monitoring and debugging
        hikariConfig.setPoolName("InventoryManagementPool");

        // Auto-commit mode for new connections
        hikariConfig.setAutoCommit(true);

        logger.debug("Creando pool de conexiones HikariCP con configuración:");
        logger.debug("  - Tamaño máximo: {}", CONNECTION_POOL_SIZE);
        logger.debug("  - Mínimo inactivo: {}", CONNECTION_POOL_MIN_IDLE);
        logger.debug("  - Timeout de conexión: {}ms", CONNECTION_TIMEOUT_MS);

        return new HikariDataSource(hikariConfig);
    }

    /**
     * Obtains a connection from the HikariCP pool.
     *
     * This method is preferred over DriverManager.getConnection() because:
     * - Connections are reused from the pool (much faster)
     * - Connection lifecycle is automatically managed
     * - Better resource utilization
     * - Improved overall application performance
     *
     * Connection should be closed using try-with-resources:
     * <pre>
     * try (Connection connection = DatabaseConnection.getConnection()) {
     *     // Use connection
     * } catch (SQLException e) {
     *     // Handle error
     * }
     * </pre>
     *
     * @return A connection from the HikariCP pool
     * @throws SQLException if a connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        logger.debug("Obteniendo conexión del pool HikariCP");
        return dataSource.getConnection();
    }

    /**
     * Gracefully shuts down the connection pool.
     *
     * Should be called when the application is shutting down to:
     * - Close all active connections
     * - Release database resources
     * - Prevent connection leaks
     *
     * Usage in a shutdown hook:
     * <pre>
     * Runtime.getRuntime().addShutdownHook(new Thread(
     *     DatabaseConnection::closePool
     * ));
     * </pre>
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Cerrando pool de conexiones...");
            dataSource.close();
            logger.info("✅ Pool de conexiones cerrado exitosamente");
        }
    }

    /**
     * Returns the status and statistics of the connection pool.
     *
     * Useful for monitoring and debugging connection pool health.
     *
     * @return String containing pool statistics
     */
    public static String getPoolStatus() {
        return String.format(
                """
                        📊 Estado del Pool HikariCP:
                          - Conexiones activas: %d
                          - Conexiones inactivas: %d
                          - Conexiones totales: %d
                          - Tamaño máximo: %d
                        """,
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getMaximumPoolSize()
        );
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseConnection() {
        throw new AssertionError("DatabaseConnection no puede ser instanciado");
    }
}
