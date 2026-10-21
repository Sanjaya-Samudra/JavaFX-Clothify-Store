package dao;

import java.sql.SQLException;
import java.util.Map;

public interface ReportDao {
    Map<String, Double> getWeeklySalesTrend() throws SQLException;
    Map<String, Double> getMonthlySalesTrend() throws SQLException;
    Map<String, Integer> getTopSellingProducts(int limit) throws SQLException;
    Double getTotalInventoryValuation() throws SQLException;
    int getTotalStockUnits() throws SQLException;
}
