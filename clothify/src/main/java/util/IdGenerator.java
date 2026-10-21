package util;

import controller.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Enterprise ID generator that automatically creates sequential, human-friendly primary keys.
 */
public class IdGenerator {

    public static String generateItemId() {
        return generateCustomId("item", "item_code", "P", 3);
    }

    public static String generateOrderId() {
        return generateCustomId("orders", "order_id", "ORD-", 4);
    }

    public static String generateSupplierId() {
        return generateCustomId("supplier", "supplier_id", "SUP-", 3);
    }

    public static String generateEmployeeId() {
        return generateCustomId("employee", "employee_id", "EMP-", 3);
    }

    public static String generateUserId() {
        return generateCustomId("user", "id", "USR-", 3);
    }

    public static String generateReturnId() {
        return generateCustomId("order_returns", "return_id", "RET-", 3);
    }

    private static String generateCustomId(String table, String column, String prefix, int padLength) {
        String sql = "SELECT " + column + " FROM " + table + " ORDER BY " + column + " DESC LIMIT 1";
        try {
            ResultSet rs = CrudUtil.execute(sql);
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null && lastId.startsWith(prefix)) {
                    String numberPart = lastId.substring(prefix.length());
                    try {
                        int num = Integer.parseInt(numberPart) + 1;
                        return String.format("%s%0" + padLength + "d", prefix, num);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating ID: " + e.getMessage());
        }
        return String.format("%s%0" + padLength + "d", prefix, 1);
    }
}
