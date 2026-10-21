package controller.report;

import dao.EmployeeDao;
import dao.ItemDao;
import dao.OrderDao;
import dao.ReportDao;
import dao.SupplierDao;
import dao.impl.EmployeeDaoImpl;
import dao.impl.ItemDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.ReportDaoImpl;
import dao.impl.SupplierDaoImpl;
import model.DashboardSummary;
import model.Item;
import model.Order;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportController implements ReportService {

    private static ReportController instance;

    private final ReportDao reportDao = new ReportDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    private final ItemDao itemDao = new ItemDaoImpl();
    private final SupplierDao supplierDao = new SupplierDaoImpl();
    private final EmployeeDao employeeDao = new EmployeeDaoImpl();

    private ReportController() {}

    public static synchronized ReportController getInstance() {
        if (instance == null) {
            instance = new ReportController();
        }
        return instance;
    }

    @Override
    public DashboardSummary getDashboardSummary() {
        DashboardSummary summary = new DashboardSummary();
        try {
            LocalDate today = LocalDate.now();
            summary.setTodaySales(orderDao.getTodaySales(today));
            summary.setTodayOrdersCount(orderDao.getTodayOrdersCount(today));
            summary.setTotalProducts(itemDao.getTotalProductCount());

            List<Item> lowStock = itemDao.getLowStockItems(5);
            summary.setLowStockCount(lowStock.size());
            summary.setLowStockItems(lowStock);

            summary.setTotalSuppliers(supplierDao.getTotalSupplierCount());
            summary.setTotalEmployees(employeeDao.getTotalEmployeeCount());

            summary.setWeeklySalesTrend(reportDao.getWeeklySalesTrend());
            summary.setRecentOrders(orderDao.getRecentOrders(6));
        } catch (SQLException e) {
            System.err.println("Error generating dashboard summary: " + e.getMessage());
            summary.setTodaySales(0.0);
            summary.setTodayOrdersCount(0);
            summary.setTotalProducts(0);
            summary.setLowStockCount(0);
            summary.setTotalSuppliers(0);
            summary.setTotalEmployees(0);
            summary.setWeeklySalesTrend(new HashMap<>());
            summary.setLowStockItems(new ArrayList<>());
            summary.setRecentOrders(new ArrayList<>());
        }
        return summary;
    }

    @Override
    public Map<String, Double> getWeeklySalesTrend() {
        try {
            return reportDao.getWeeklySalesTrend();
        } catch (SQLException e) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Double> getMonthlySalesTrend() {
        try {
            return reportDao.getMonthlySalesTrend();
        } catch (SQLException e) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Integer> getTopSellingProducts(int limit) {
        try {
            return reportDao.getTopSellingProducts(limit);
        } catch (SQLException e) {
            return new HashMap<>();
        }
    }

    @Override
    public Double getTotalInventoryValuation() {
        try {
            return reportDao.getTotalInventoryValuation();
        } catch (SQLException e) {
            return 0.0;
        }
    }

    @Override
    public int getTotalStockUnits() {
        try {
            return reportDao.getTotalStockUnits();
        } catch (SQLException e) {
            return 0;
        }
    }
}
