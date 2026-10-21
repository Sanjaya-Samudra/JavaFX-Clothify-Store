package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummary {
    private Double todaySales;
    private Integer todayOrdersCount;
    private Integer totalProducts;
    private Integer lowStockCount;
    private Integer totalSuppliers;
    private Integer totalEmployees;
    private Map<String, Double> weeklySalesTrend; // "Monday" -> 12000.0, etc.
    private List<Item> lowStockItems;
    private List<Order> recentOrders;
}
