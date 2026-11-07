package test;

import db.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDatabase {
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to database...");
            Connection conn = DBConnection.getInstance().getConnection();
            System.out.println("DB Connection Successful!");
            
            db.DBInitializer.initializeDatabase();
            conn = DBConnection.getInstance().getConnection();

            // Ensure image column is LONGBLOB to support high-resolution photos
            try (Statement s = conn.createStatement()) {
                s.execute("ALTER TABLE item MODIFY COLUMN image LONGBLOB");
                System.out.println("Image column verified as LONGBLOB!");
            } catch (Exception e) {
                System.out.println("Image column alter note: " + e.getMessage());
            }

            conn = DBConnection.getInstance().getConnection();
            ResultSet rs = conn.getMetaData().getTables("clothify", null, "%", null);
            System.out.println("\n--- TABLES IN CLOTHIFY ---");
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("TABLE: " + tableName);
            }

            // Check users
            try (Statement s = conn.createStatement(); ResultSet urs = s.executeQuery("SELECT id, email, name, role, permissions FROM user")) {
                System.out.println("\n--- USERS IN DB ---");
                while (urs.next()) {
                    System.out.println("User: id=" + urs.getString("id") + ", email=" + urs.getString("email") + ", name=" + urs.getString("name") + ", role=" + urs.getString("role") + ", perms=" + urs.getString("permissions"));
                }
            }

            // Check items
            try (Statement s = conn.createStatement(); ResultSet irs = s.executeQuery("SELECT item_code, name, price, quantity, category, (image IS NOT NULL) AS has_img FROM item")) {
                System.out.println("\n--- ITEMS IN DB ---");
                while (irs.next()) {
                    System.out.println("Item: code=" + irs.getString("item_code") + ", name=" + irs.getString("name") + ", price=" + irs.getDouble("price") + ", qty=" + irs.getInt("quantity") + ", hasImg=" + irs.getBoolean("has_img"));
                }
            }

            System.out.println("\nALL CHECKS PASSED SUCCESSFULLY!");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Database check failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
