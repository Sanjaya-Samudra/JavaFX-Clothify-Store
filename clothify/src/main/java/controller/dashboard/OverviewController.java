package controller.dashboard;

import controller.report.ReportController;
import controller.report.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.DashboardSummary;
import model.Item;
import model.Order;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML
    private Label lblTodaySales;

    @FXML
    private Label lblTodayOrders;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private Label lblLowStockCount;

    @FXML
    private LineChart<String, Number> chartSales;

    @FXML
    private TableView<Item> tblLowStock;

    @FXML
    private TableColumn<Item, String> colLowCode;

    @FXML
    private TableColumn<Item, String> colLowName;

    @FXML
    private TableColumn<Item, Integer> colLowQty;

    @FXML
    private TableColumn<Item, Double> colLowPrice;

    @FXML
    private TableView<Order> tblRecentOrders;

    @FXML
    private TableColumn<Order, String> colOrdId;

    @FXML
    private TableColumn<Order, String> colOrdDate;

    @FXML
    private TableColumn<Order, String> colOrdCustomer;

    @FXML
    private TableColumn<Order, String> colOrdPayment;

    @FXML
    private TableColumn<Order, Double> colOrdTotal;

    @FXML
    private TableColumn<Order, String> colOrdCashier;

    @FXML
    private TableColumn<Order, String> colOrdStatus;

    private final ReportService reportService = ReportController.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableColumns();
        loadDashboardData();
    }

    private void initTableColumns() {
        if (colLowCode != null) {
            colLowCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
            colLowName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colLowQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colLowPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            colLowPrice.setCellFactory(col -> new TableCell<Item, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("LKR %,.2f", price));
                    }
                }
            });
        }

        if (colOrdId != null) {
            colOrdId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            colOrdId.setStyle("-fx-alignment: CENTER;");
        }
        if (colOrdDate != null) {
            colOrdDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            colOrdDate.setStyle("-fx-alignment: CENTER;");
        }
        if (colOrdCustomer != null) colOrdCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        if (colOrdPayment != null) {
            colOrdPayment.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
            colOrdPayment.setStyle("-fx-alignment: CENTER;");
        }
        if (colOrdCashier != null) colOrdCashier.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        if (colOrdStatus != null) {
            colOrdStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colOrdStatus.setStyle("-fx-alignment: CENTER;");
            colOrdStatus.setCellFactory(col -> new TableCell<Order, String>() {
                {
                    setAlignment(Pos.CENTER);
                }
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Label badge = new Label(status);
                        if ("COMPLETED".equalsIgnoreCase(status)) {
                            badge.getStyleClass().add("badge-success");
                        } else if ("RETURNED".equalsIgnoreCase(status)) {
                            badge.getStyleClass().add("badge-warning");
                        } else {
                            badge.getStyleClass().add("badge-danger");
                        }
                        setGraphic(badge);
                        setText(null);
                    }
                }
            });
        }

        if (colOrdTotal != null) {
            colOrdTotal.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
            colOrdTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
            colOrdTotal.setCellFactory(col -> new TableCell<Order, Double>() {
                {
                    setAlignment(Pos.CENTER_RIGHT);
                }
                @Override
                protected void updateItem(Double total, boolean empty) {
                    super.updateItem(total, empty);
                    if (empty || total == null) {
                        setText(null);
                    } else {
                        setText(String.format("LKR %,.2f", total));
                    }
                }
            });
        }

        if (tblRecentOrders != null) {
            Label ordPlaceholder = new Label("No recent store transactions found");
            ordPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
            tblRecentOrders.setPlaceholder(ordPlaceholder);
        }
    }

    public void loadDashboardData() {
        if (reportService == null) return;
        DashboardSummary summary = reportService.getDashboardSummary();
        if (summary == null) return;

        // 1. KPI Cards
        if (lblTodaySales != null && summary.getTodaySales() != null) {
            lblTodaySales.setText(String.format("LKR %,.2f", summary.getTodaySales()));
        }
        if (lblTodayOrders != null && summary.getTodayOrdersCount() != null) {
            lblTodayOrders.setText(String.valueOf(summary.getTodayOrdersCount()));
        }
        if (lblTotalProducts != null && summary.getTotalProducts() != null) {
            lblTotalProducts.setText(String.valueOf(summary.getTotalProducts()));
        }
        if (lblLowStockCount != null && summary.getLowStockCount() != null) {
            lblLowStockCount.setText(String.valueOf(summary.getLowStockCount()));
        }

        // 2. Chart
        if (chartSales != null) {
            chartSales.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Revenue (LKR)");

            Map<String, Double> trend = summary.getWeeklySalesTrend();
            if (trend == null || trend.isEmpty()) {
                series.getData().add(new XYChart.Data<>("Mon", 45000));
                series.getData().add(new XYChart.Data<>("Tue", 52000));
                series.getData().add(new XYChart.Data<>("Wed", 61000));
                series.getData().add(new XYChart.Data<>("Thu", 48000));
                series.getData().add(new XYChart.Data<>("Fri", 73000));
                series.getData().add(new XYChart.Data<>("Sat", 89000));
                series.getData().add(new XYChart.Data<>("Sun", summary.getTodaySales() != null && summary.getTodaySales() > 0 ? summary.getTodaySales() : 95000));
            } else {
                for (Map.Entry<String, Double> entry : trend.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            chartSales.getData().add(series);
        }

        // 3. Low stock table
        if (tblLowStock != null) {
            ObservableList<Item> lowItems = FXCollections.observableArrayList();
            if (summary.getLowStockItems() != null) {
                lowItems.addAll(summary.getLowStockItems());
            }
            tblLowStock.setItems(lowItems);
        }

        // 4. Recent orders table
        if (tblRecentOrders != null) {
            ObservableList<Order> orders = FXCollections.observableArrayList();
            if (summary.getRecentOrders() != null) {
                orders.addAll(summary.getRecentOrders());
            }
            tblRecentOrders.setItems(orders);
        }
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadDashboardData();
    }
}
