package db;

import security.PasswordSecurity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automatically initializes database schema and seeds initial master data if empty.
 * Guarantees zero-config setup on any environment with salted, secure password hashes.
 */
public class DBInitializer {

    public static void initializeDatabase() {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (Statement stmt = conn.createStatement()) {

                // 1. Ensure User table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user (" +
                        "id VARCHAR(20) PRIMARY KEY, " +
                        "email VARCHAR(100) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "title VARCHAR(10), " +
                        "name VARCHAR(100) NOT NULL, " +
                        "dob DATE, " +
                        "salary DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                        "address VARCHAR(255), " +
                        "city VARCHAR(50), " +
                        "province VARCHAR(50), " +
                        "role VARCHAR(20) NOT NULL DEFAULT 'ADMIN', " +
                        "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                        "permissions VARCHAR(255) NOT NULL DEFAULT 'ORDER,PRODUCT,REPORT'" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // Migration check: ensure user columns exist
                ensureColumnExists(conn, "user", "role", "VARCHAR(20) NOT NULL DEFAULT 'ADMIN'");
                ensureColumnExists(conn, "user", "status", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'");
                ensureColumnExists(conn, "user", "permissions", "VARCHAR(255) NOT NULL DEFAULT 'ORDER,PRODUCT,REPORT'");
                try {
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN id VARCHAR(20) NOT NULL");
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN email VARCHAR(100) NOT NULL");
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN password VARCHAR(255) NOT NULL");
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN name VARCHAR(100) NOT NULL");
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN address VARCHAR(255)");
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN city VARCHAR(50)");
                    stmt.executeUpdate("ALTER TABLE user MODIFY COLUMN province VARCHAR(50)");
                } catch (Exception ignored) {}
                try {
                    stmt.executeUpdate("UPDATE user SET permissions='ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER' WHERE role='ADMIN' AND (permissions IS NULL OR permissions='ORDER,PRODUCT,REPORT')");
                } catch (Exception ignored) {}

                // 2. Ensure Item (Product) table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS item (" +
                        "item_code VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "size VARCHAR(50), " +
                        "price DECIMAL(10,2) NOT NULL, " +
                        "quantity INT NOT NULL, " +
                        "category VARCHAR(50), " +
                        "supplier VARCHAR(100), " +
                        "image LONGBLOB" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 3. Ensure Supplier table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS supplier (" +
                        "supplier_id VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "company VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "contact VARCHAR(20) NOT NULL, " +
                        "item_category VARCHAR(100), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 4. Ensure Employee table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee (" +
                        "employee_id VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "contact VARCHAR(20) NOT NULL, " +
                        "company VARCHAR(100), " +
                        "role VARCHAR(50) NOT NULL, " +
                        "salary DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 5. Ensure Orders table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders (" +
                        "order_id VARCHAR(20) PRIMARY KEY, " +
                        "order_date DATE NOT NULL, " +
                        "order_time TIME NOT NULL, " +
                        "customer_name VARCHAR(100), " +
                        "customer_email VARCHAR(100), " +
                        "customer_phone VARCHAR(20), " +
                        "payment_type VARCHAR(20) NOT NULL, " +
                        "sub_total DECIMAL(10,2) NOT NULL, " +
                        "discount DECIMAL(10,2) DEFAULT 0.00, " +
                        "total_cost DECIMAL(10,2) NOT NULL, " +
                        "employee_id VARCHAR(20), " +
                        "employee_name VARCHAR(100), " +
                        "status VARCHAR(20) DEFAULT 'COMPLETED', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 6. Ensure Order Details table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS order_details (" +
                        "detail_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "order_id VARCHAR(20) NOT NULL, " +
                        "item_code VARCHAR(20) NOT NULL, " +
                        "item_name VARCHAR(100) NOT NULL, " +
                        "unit_price DECIMAL(10,2) NOT NULL, " +
                        "qty INT NOT NULL, " +
                        "total DECIMAL(10,2) NOT NULL, " +
                        "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 7. Ensure Order Returns table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS order_returns (" +
                        "return_id VARCHAR(20) PRIMARY KEY, " +
                        "order_id VARCHAR(20) NOT NULL, " +
                        "item_code VARCHAR(20) NOT NULL, " +
                        "return_qty INT NOT NULL, " +
                        "reason VARCHAR(255), " +
                        "refund_amount DECIMAL(10,2) NOT NULL, " +
                        "return_date DATE NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // Remove legacy test dummy users
                try {
                    stmt.executeUpdate("DELETE FROM user WHERE email IN ('one@gmail.com', 'two@gmail.com')");
                } catch (Exception ignored) {}

                // Seed default admin and cashier if user table has no administrator
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user WHERE role='ADMIN'")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        String adminPassHash = PasswordSecurity.hashPassword("admin123");
                        String cashierPassHash = PasswordSecurity.hashPassword("cashier123");

                        String seedSql = "INSERT INTO user (id, email, password, title, name, dob, salary, address, city, province, role, status, permissions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(seedSql)) {
                            // Seed Admin
                            ps.setString(1, "USR-001");
                            ps.setString(2, "admin@clothify.com");
                            ps.setString(3, adminPassHash);
                            ps.setString(4, "Mr");
                            ps.setString(5, "System Administrator");
                            ps.setDate(6, java.sql.Date.valueOf("1995-01-01"));
                            ps.setDouble(7, 85000.00);
                            ps.setString(8, "Main Tower, City Center");
                            ps.setString(9, "Colombo");
                            ps.setString(10, "Western");
                            ps.setString(11, "ADMIN");
                            ps.setString(12, "ACTIVE");
                            ps.setString(13, "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
                            ps.executeUpdate();

                            // Seed Cashier
                            ps.setString(1, "USR-002");
                            ps.setString(2, "cashier@clothify.com");
                            ps.setString(3, cashierPassHash);
                            ps.setString(4, "Ms");
                            ps.setString(5, "Sarah Jenkins");
                            ps.setDate(6, java.sql.Date.valueOf("1998-05-12"));
                            ps.setDouble(7, 45000.00);
                            ps.setString(8, "Clothify City Mall");
                            ps.setString(9, "Colombo");
                            ps.setString(10, "Western");
                            ps.setString(11, "CASHIER");
                            ps.setString(12, "ACTIVE");
                            ps.setString(13, "ORDER,PRODUCT,REPORT");
                            ps.executeUpdate();
                        }
                    }
                }
            }
            System.out.println("Clothify Database Initialized Successfully!");
        } catch (SQLException e) {
            System.err.println("Warning during DB initialization: " + e.getMessage());
        }
    }

    private static void ensureColumnExists(Connection conn, String table, String column, String colDef) {
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SHOW COLUMNS FROM " + table + " LIKE '" + column + "'")) {
            if (!rs.next()) {
                s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + colDef);
            }
        } catch (SQLException ignored) {}
    }
}
