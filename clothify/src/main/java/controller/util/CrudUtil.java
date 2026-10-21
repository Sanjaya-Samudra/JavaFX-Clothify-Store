package controller.util;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Enterprise-grade CRUD Execution Utility.
 * Guarantees zero resource leaks by closing PreparedStatements on updates and
 * configuring closeOnCompletion on queries.
 */
public class CrudUtil {

    @SuppressWarnings("unchecked")
    public static <T> T execute(String SQL, Object... args) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement psTm = conn.prepareStatement(SQL);

        for (int i = 0; i < args.length; i++) {
            psTm.setObject(i + 1, args[i]);
        }

        String trimmed = SQL.trim();
        boolean isQuery = trimmed.regionMatches(true, 0, "SELECT", 0, 6)
                || trimmed.regionMatches(true, 0, "SHOW", 0, 4)
                || trimmed.regionMatches(true, 0, "DESCRIBE", 0, 8);

        if (isQuery) {
            try {
                // When caller closes the returned ResultSet, the PreparedStatement is automatically closed.
                psTm.closeOnCompletion();
                return (T) psTm.executeQuery();
            } catch (SQLException e) {
                try {
                    psTm.close();
                } catch (SQLException ignored) {}
                throw e;
            }
        } else {
            try {
                return (T) (Boolean) (psTm.executeUpdate() > 0);
            } finally {
                try {
                    psTm.close();
                } catch (SQLException ignored) {}
            }
        }
    }
}
