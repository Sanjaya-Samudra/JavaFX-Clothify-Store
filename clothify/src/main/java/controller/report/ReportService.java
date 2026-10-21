package controller.report;

import model.DashboardSummary;

import java.util.Map;

public interface ReportService {
    DashboardSummary getDashboardSummary();
    Map<String, Double> getWeeklySalesTrend();
    Map<String, Double> getMonthlySalesTrend();
    Map<String, Integer> getTopSellingProducts(int limit);
    Double getTotalInventoryValuation();
    int getTotalStockUnits();
}
