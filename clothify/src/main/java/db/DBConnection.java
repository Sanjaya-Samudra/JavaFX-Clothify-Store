package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Resilient Database Connection Singleton for Clothify POS & ERP System.
 * Supports configurable connection properties (environment variables, system properties, or config file),
 * automatic reconnection, connection health validation, atomic transaction demarcation, and clean shutdown.
 */
public class DBConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/clothify?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "2003";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;

    private static DBConnection instance;
    private Connection connection;

    static {
        loadConfiguration();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.closeQuietly();
            }
        }));
    }

    private static void loadConfiguration() {
        Properties props = new Properties();

        // 1. Try config/database.properties in working directory or classpath
        File cfgFile = new File("config/database.properties");
        if (cfgFile.exists() && cfgFile.isFile()) {
            try (InputStream in = new FileInputStream(cfgFile)) {
                props.load(in);
            } catch (Exception ignored) {}
        } else {
            try (InputStream in = DBConnection.class.getResourceAsStream("/database.properties")) {
                if (in != null) {
                    props.load(in);
                }
            } catch (Exception ignored) {}
        }

        // 2. Resolve precedence: System Property > Env Var > Properties File > Default
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        dbUrl = System.getProperty("db.url", envUrl != null ? envUrl : props.getProperty("db.url", DEFAULT_URL));
        dbUser = System.getProperty("db.user", envUser != null ? envUser : props.getProperty("db.user", DEFAULT_USER));
        dbPass = System.getProperty("db.password", envPass != null ? envPass : props.getProperty("db.password", DEFAULT_PASS));
    }

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        }
        connect();
    }

    private void connect() throws SQLException {
        this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        try (java.sql.Statement s = this.connection.createStatement()) {
            s.execute("ALTER TABLE item MODIFY COLUMN image LONGBLOB");
        } catch (Exception ignored) {
            // Already LONGBLOB or table created later
        }
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                closeQuietly();
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to restore database connection: " + e.getMessage(), e);
        }
        return connection;
    }

    public static synchronized DBConnection getInstance() throws SQLException {
        if (instance == null || instance.connection == null) {
            instance = new DBConnection();
        } else {
            try {
                if (instance.connection.isClosed() || !instance.connection.isValid(2)) {
                    instance.closeQuietly();
                    instance = new DBConnection();
                }
            } catch (SQLException e) {
                instance.closeQuietly();
                instance = new DBConnection();
            }
        }
        return instance;
    }

    public synchronized void startTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public synchronized void commit() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public synchronized void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Rollback failed: " + e.getMessage());
        }
    }

    public synchronized void closeQuietly() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {}
            connection = null;
        }
    }
}
