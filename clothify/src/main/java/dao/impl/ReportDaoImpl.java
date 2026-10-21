package dao.impl;

import controller.util.CrudUtil;
import dao.ReportDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDaoImpl implements ReportDao {

    @Override
    public Map<String, Double> getWeeklySalesTrend() throws SQLException {
        Map<String, Double> trend = new LinkedHashMap<>();
        // Last 7 days sales
        String sql = "SELECT DATE_FORMAT(order_date, '%a %d') AS day_label, SUM(total_cost) AS total " +
                "FROM orders " +
                "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND status != 'CANCELLED' " +
                "GROUP BY order_date " +
                "ORDER BY order_date ASC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                trend.put(rs.getString("day_label"), rs.getDouble("total"));
            }
        }
        return trend;
    }

    @Override
    public Map<String, Double> getMonthlySalesTrend() throws SQLException {
        Map<String, Double> trend = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%b %Y') AS month_label, SUM(total_cost) AS total " +
                "FROM orders " +
                "WHERE status != 'CANCELLED' " +
                "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                "ORDER BY MIN(order_date) ASC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                trend.put(rs.getString("month_label"), rs.getDouble("total"));
            }
        }
        return trend;
    }

    @Override
    public Map<String, Integer> getTopSellingProducts(int limit) throws SQLException {
        Map<String, Integer> top = new LinkedHashMap<>();
        String sql = "SELECT item_name, SUM(qty) AS total_qty FROM order_details GROUP BY item_code, item_name ORDER BY total_qty DESC LIMIT ?";
        try (ResultSet rs = CrudUtil.execute(sql, limit)) {
            while (rs.next()) {
                top.put(rs.getString("item_name"), rs.getInt("total_qty"));
            }
        }
        return top;
    }

    @Override
    public Double getTotalInventoryValuation() throws SQLException {
        String sql = "SELECT SUM(price * quantity) FROM item";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        }
    }

    @Override
    public int getTotalStockUnits() throws SQLException {
        String sql = "SELECT SUM(quantity) FROM item";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
