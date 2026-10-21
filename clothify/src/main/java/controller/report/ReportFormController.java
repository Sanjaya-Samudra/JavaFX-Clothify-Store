package controller.report;

import controller.employee.EmployeeController;
import controller.item.ItemController;
import controller.order.OrderController;
import controller.supplier.SupplierController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Employee;
import model.Item;
import model.Order;
import model.Supplier;
import util.AlertUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportFormController implements Initializable {

    @FXML
    private Label lblInventoryValuation;

    @FXML
    private Label lblTotalUnits;

    @FXML
    private Label lblVendorsStaff;

    @FXML
    private BarChart<String, Number> chartMonthly;

    @FXML
    private BarChart<String, Number> chartTopProducts;

    @FXML
    private TextArea txtReportPreview;

    private final ReportService reportService = ReportController.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnalytics();
        btnSalesSummaryReportOnAction(null);
    }

    public void loadAnalytics() {
        Double val = reportService.getTotalInventoryValuation();
        lblInventoryValuation.setText(String.format("LKR %,.2f", val != null ? val : 0.0));

        int units = reportService.getTotalStockUnits();
        lblTotalUnits.setText(String.format("%,d", units));

        int supCount = SupplierController.getInstance().getTotalSupplierCount();
        int empCount = EmployeeController.getInstance().getTotalEmployeeCount();
        lblVendorsStaff.setText(supCount + " Suppliers • " + empCount + " Staff");

        // Load monthly chart
        chartMonthly.getData().clear();
        XYChart.Series<String, Number> monthSeries = new XYChart.Series<>();
        Map<String, Double> monthly = reportService.getMonthlySalesTrend();
        if (monthly.isEmpty()) {
            monthSeries.getData().add(new XYChart.Data<>("Jul 2026", 185000));
            monthSeries.getData().add(new XYChart.Data<>("Aug 2026", 240000));
            monthSeries.getData().add(new XYChart.Data<>("Sep 2026", 310000));
            monthSeries.getData().add(new XYChart.Data<>("Oct 2026", 385000));
        } else {
            for (Map.Entry<String, Double> entry : monthly.entrySet()) {
                monthSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        chartMonthly.getData().add(monthSeries);

        // Load top products chart
        chartTopProducts.getData().clear();
        XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
        Map<String, Integer> top = reportService.getTopSellingProducts(5);
        if (top.isEmpty()) {
            topSeries.getData().add(new XYChart.Data<>("Oxford Shirt", 42));
            topSeries.getData().add(new XYChart.Data<>("Chino Trousers", 36));
            topSeries.getData().add(new XYChart.Data<>("Maxi Dress", 28));
            topSeries.getData().add(new XYChart.Data<>("Leather Belt", 24));
        } else {
            for (Map.Entry<String, Integer> entry : top.entrySet()) {
                String name = entry.getKey();
                if (name.length() > 14) name = name.substring(0, 12) + "..";
                topSeries.getData().add(new XYChart.Data<>(name, entry.getValue()));
            }
        }
        chartTopProducts.getData().add(topSeries);
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadAnalytics();
    }

    @FXML
    void btnSalesSummaryReportOnAction(ActionEvent event) {
        StringBuilder sb = new StringBuilder();
        String line = "========================================================================================\n";
        String dashed = "----------------------------------------------------------------------------------------\n";

        sb.append(line);
        sb.append("                         CLOTHIFY STORE - SALES SUMMARY REPORT                          \n");
        sb.append("                       Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append(line);
        sb.append(String.format(" %-12s | %-12s | %-24s | %-10s | %14s | %-10s\n",
                "ORDER ID", "DATE", "CUSTOMER", "PAYMENT", "NET TOTAL", "STATUS"));
        sb.append(dashed);

        List<Order> orders = OrderController.getInstance().getAllOrders();
        double grandTotal = 0.0;
        for (Order o : orders) {
            sb.append(String.format(" %-12s | %-12s | %-24s | %-10s | %14.2f | %-10s\n",
                    o.getOrderId(),
                    o.getOrderDate(),
                    o.getCustomerName() != null ? o.getCustomerName() : "Walk-in Customer",
                    o.getPaymentType(),
                    o.getTotalCost(),
                    o.getStatus()));
            grandTotal += o.getTotalCost();
        }
        sb.append(dashed);
        sb.append(String.format(" Total Orders: %d                                              CUMULATIVE SALES: LKR %,.2f\n", orders.size(), grandTotal));
        sb.append(line);

        txtReportPreview.setText(sb.toString());
    }

    @FXML
    void btnInventoryValuationReportOnAction(ActionEvent event) {
        StringBuilder sb = new StringBuilder();
        String line = "========================================================================================\n";
        String dashed = "----------------------------------------------------------------------------------------\n";

        sb.append(line);
        sb.append("                     CLOTHIFY STORE - INVENTORY & VALUATION AUDIT                       \n");
        sb.append("                       Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append(line);
        sb.append(String.format(" %-8s | %-30s | %-12s | %-6s | %10s | %5s | %14s\n",
                "CODE", "PRODUCT NAME", "CATEGORY", "SIZE", "UNIT PRICE", "STOCK", "TOTAL VALUE"));
        sb.append(dashed);

        List<Item> items = ItemController.getInstance().getAllItems();
        double grandValue = 0.0;
        int totalUnits = 0;
        for (Item item : items) {
            double totalVal = item.getPrice() * item.getQuantity();
            sb.append(String.format(" %-8s | %-30s | %-12s | %-6s | %10.2f | %5d | %14.2f\n",
                    item.getItemCode(),
                    item.getName().length() > 30 ? item.getName().substring(0, 28) + ".." : item.getName(),
                    item.getCategory(),
                    item.getSize() != null ? item.getSize() : "-",
                    item.getPrice(),
                    item.getQuantity(),
                    totalVal));
            grandValue += totalVal;
            totalUnits += item.getQuantity();
        }
        sb.append(dashed);
        sb.append(String.format(" Total SKUs: %d | Total Stock Units: %d               TOTAL VALUATION: LKR %,.2f\n", items.size(), totalUnits, grandValue));
        sb.append(line);

        txtReportPreview.setText(sb.toString());
    }

    @FXML
    void btnSupplierReportOnAction(ActionEvent event) {
        StringBuilder sb = new StringBuilder();
        String line = "========================================================================================\n";
        String dashed = "----------------------------------------------------------------------------------------\n";

        sb.append(line);
        sb.append("                     CLOTHIFY STORE - VENDOR & SUPPLIER DIRECTORY                       \n");
        sb.append("                       Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append(line);
        sb.append(String.format(" %-10s | %-24s | %-24s | %-15s | %-20s\n",
                "SUP ID", "VENDOR NAME", "COMPANY", "CONTACT", "CATEGORY"));
        sb.append(dashed);

        List<Supplier> suppliers = SupplierController.getInstance().getAllSuppliers();
        for (Supplier s : suppliers) {
            sb.append(String.format(" %-10s | %-24s | %-24s | %-15s | %-20s\n",
                    s.getSupplierId(),
                    s.getName(),
                    s.getCompany(),
                    s.getContact(),
                    s.getItemCategory()));
        }
        sb.append(dashed);
        sb.append(String.format(" Total Registered Suppliers: %d\n", suppliers.size()));
        sb.append(line);

        txtReportPreview.setText(sb.toString());
    }

    @FXML
    void btnEmployeeReportOnAction(ActionEvent event) {
        StringBuilder sb = new StringBuilder();
        String line = "========================================================================================\n";
        String dashed = "----------------------------------------------------------------------------------------\n";

        sb.append(line);
        sb.append("                      CLOTHIFY STORE - STAFF & PAYROLL DIRECTORY                        \n");
        sb.append("                       Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append(line);
        sb.append(String.format(" %-10s | %-26s | %-28s | %-15s | %12s\n",
                "EMP ID", "NAME", "ROLE / DESIGNATION", "CONTACT", "SALARY"));
        sb.append(dashed);

        List<Employee> employees = EmployeeController.getInstance().getAllEmployees();
        double totalPayroll = 0.0;
        for (Employee e : employees) {
            sb.append(String.format(" %-10s | %-26s | %-28s | %-15s | %12.2f\n",
                    e.getEmployeeId(),
                    e.getName(),
                    e.getRole(),
                    e.getContact(),
                    e.getSalary()));
            totalPayroll += e.getSalary();
        }
        sb.append(dashed);
        sb.append(String.format(" Total Active Employees: %d                               TOTAL MONTHLY PAYROLL: LKR %,.2f\n", employees.size(), totalPayroll));
        sb.append(line);

        txtReportPreview.setText(sb.toString());
    }

    @FXML
    void btnSaveReportToFileOnAction(ActionEvent event) {
        String content = txtReportPreview.getText();
        if (content == null || content.isEmpty()) {
            AlertUtil.showWarning("Empty Report", "Please generate a report first.");
            return;
        }

        File reportsDir = new File("reports");
        if (!reportsDir.exists()) reportsDir.mkdirs();

        String fileName = "Clothify_Report_" + System.currentTimeMillis() + ".txt";
        File file = new File(reportsDir, fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            AlertUtil.showSuccess("Report Saved", "Report successfully written to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            AlertUtil.showError("Save Failed", "Could not save report file: " + e.getMessage());
        }
    }
}
