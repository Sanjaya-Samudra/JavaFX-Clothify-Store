package controller.order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Order;
import model.OrderDetail;
import model.OrderReturn;
import util.AlertUtil;
import util.IdGenerator;
import util.ReceiptGenerator;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrderHistoryController implements Initializable {

    @FXML
    private TextField txtSearchOrder;

    @FXML
    private Label lblTotalOrdersCount;

    @FXML
    private Label lblCompletedCount;

    @FXML
    private Label lblReturnedCount;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private TableView<Order> tblOrders;

    @FXML
    private TableColumn<Order, String> colOrderId;

    @FXML
    private TableColumn<Order, LocalDate> colDate;

    @FXML
    private TableColumn<Order, LocalTime> colTime;

    @FXML
    private TableColumn<Order, String> colCustomer;

    @FXML
    private TableColumn<Order, String> colPayment;

    @FXML
    private TableColumn<Order, Double> colSubtotal;

    @FXML
    private TableColumn<Order, Double> colDiscount;

    @FXML
    private TableColumn<Order, Double> colTotal;

    @FXML
    private TableColumn<Order, String> colCashier;

    @FXML
    private TableColumn<Order, String> colStatus;

    private final OrderService orderService = OrderController.getInstance();
    private ObservableList<Order> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        loadOrders();
    }

    private void initColumns() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));

        colOrderId.setStyle("-fx-alignment: CENTER;");
        colDate.setStyle("-fx-alignment: CENTER;");
        colTime.setStyle("-fx-alignment: CENTER;");
        colPayment.setStyle("-fx-alignment: CENTER;");
        colStatus.setStyle("-fx-alignment: CENTER;");

        colTime.setCellFactory(col -> new TableCell<Order, LocalTime>() {
            private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(time.format(timeFmt));
                }
            }
        });
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colCashier.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colSubtotal.setCellFactory(col -> createCurrencyCell());
        colDiscount.setCellFactory(col -> createCurrencyCell());
        colTotal.setCellFactory(col -> createCurrencyCell());

        colStatus.setCellFactory(col -> new TableCell<Order, String>() {
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

        Label orderPlaceholder = new Label("No matching sales orders found");
        orderPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        tblOrders.setPlaceholder(orderPlaceholder);
    }

    private TableCell<Order, Double> createCurrencyCell() {
        return new TableCell<Order, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) setText(null);
                else setText(String.format("LKR %,.2f", amount));
            }
        };
    }

    public void loadOrders() {
        masterList = orderService.getAllOrders();
        tblOrders.setItems(masterList);
        updateKpiMetrics();
    }

    private void updateKpiMetrics() {
        int total = masterList != null ? masterList.size() : 0;
        int completed = 0;
        int returned = 0;
        double revenue = 0.0;

        if (masterList != null) {
            for (Order o : masterList) {
                if ("COMPLETED".equalsIgnoreCase(o.getStatus())) {
                    completed++;
                    if (o.getTotalCost() != null) {
                        revenue += o.getTotalCost();
                    }
                } else if ("RETURNED".equalsIgnoreCase(o.getStatus())) {
                    returned++;
                }
            }
        }

        if (lblTotalOrdersCount != null) lblTotalOrdersCount.setText(String.valueOf(total));
        if (lblCompletedCount != null) lblCompletedCount.setText(String.valueOf(completed));
        if (lblReturnedCount != null) lblReturnedCount.setText(String.valueOf(returned));
        if (lblTotalRevenue != null) lblTotalRevenue.setText(String.format("LKR %,.2f", revenue));
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadOrders();
    }

    @FXML
    void onSearchKeyReleased(KeyEvent event) {
        String query = txtSearchOrder.getText() != null ? txtSearchOrder.getText().trim().toLowerCase() : "";
        if (query.isEmpty()) {
            tblOrders.setItems(masterList);
        } else {
            ObservableList<Order> filtered = FXCollections.observableArrayList();
            for (Order o : masterList) {
                if (o.getOrderId().toLowerCase().contains(query) ||
                        (o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(query)) ||
                        (o.getPaymentType() != null && o.getPaymentType().toLowerCase().contains(query)) ||
                        (o.getCustomerPhone() != null && o.getCustomerPhone().contains(query))) {
                    filtered.add(o);
                }
            }
            tblOrders.setItems(filtered);
        }
    }

    @FXML
    void onTableClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            btnViewDetailsOnAction(null);
        }
    }

    public void focusOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) return;
        String trimmed = orderId.trim();
        txtSearchOrder.setText(trimmed);
        onSearchKeyReleased(null);
        for (Order o : tblOrders.getItems()) {
            if (o.getOrderId().equalsIgnoreCase(trimmed)) {
                tblOrders.getSelectionModel().select(o);
                tblOrders.scrollTo(o);
                break;
            }
        }
    }

    @FXML
    void btnViewDetailsOnAction(ActionEvent event) {
        Order selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an order from the table to view line items.");
            return;
        }

        List<OrderDetail> details = orderService.getOrderDetails(selected.getOrderId());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Order Details • " + selected.getOrderId());

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(16, 18, 16, 18));
        contentBox.setStyle("-fx-background-color: #ffffff;");

        // Header info box
        VBox headerCard = new VBox(8);
        headerCard.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-padding: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label("Order #" + selected.getOrderId());
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLbl.setStyle("-fx-text-fill: #0f172a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLbl = new Label(selected.getStatus());
        if ("COMPLETED".equalsIgnoreCase(selected.getStatus())) {
            statusLbl.getStyleClass().add("badge-success");
        } else if ("RETURNED".equalsIgnoreCase(selected.getStatus())) {
            statusLbl.getStyleClass().add("badge-warning");
        } else {
            statusLbl.getStyleClass().add("badge-danger");
        }
        topRow.getChildren().addAll(titleLbl, spacer, statusLbl);

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(16);
        infoGrid.setVgap(6);

        String contact = selected.getCustomerPhone() != null && !selected.getCustomerPhone().isEmpty()
                ? " (" + selected.getCustomerPhone() + ")" : "";
        infoGrid.add(createMetaLabel("Customer:"), 0, 0);
        infoGrid.add(createValueLabel(selected.getCustomerName() + contact), 1, 0);

        infoGrid.add(createMetaLabel("Date & Time:"), 0, 1);
        infoGrid.add(createValueLabel(selected.getOrderDate() + " at " + selected.getOrderTime()), 1, 1);

        infoGrid.add(createMetaLabel("Cashier:"), 2, 0);
        infoGrid.add(createValueLabel(selected.getEmployeeName()), 3, 0);

        infoGrid.add(createMetaLabel("Payment Mode:"), 2, 1);
        infoGrid.add(createValueLabel(selected.getPaymentType()), 3, 1);

        headerCard.getChildren().addAll(topRow, infoGrid);

        // Line items table
        TableView<OrderDetail> table = new TableView<>();
        TableColumn<OrderDetail, String> colCode = new TableColumn<>("Item Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colCode.setPrefWidth(95);

        TableColumn<OrderDetail, String> colName = new TableColumn<>("Item Description");
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colName.setPrefWidth(210);

        TableColumn<OrderDetail, Double> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colPrice.setPrefWidth(100);
        colPrice.setCellFactory(col -> createDetailCurrencyCell());

        TableColumn<OrderDetail, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colQty.setPrefWidth(65);

        TableColumn<OrderDetail, Double> colTot = new TableColumn<>("Line Total");
        colTot.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTot.setPrefWidth(110);
        colTot.setCellFactory(col -> createDetailCurrencyCell());

        table.getColumns().add(colCode);
        table.getColumns().add(colName);
        table.getColumns().add(colPrice);
        table.getColumns().add(colQty);
        table.getColumns().add(colTot);
        table.setItems(FXCollections.observableArrayList(details));
        table.setPrefHeight(230);
        table.setPrefWidth(600);

        // Bottom Totals Card
        HBox bottomBar = new HBox(16);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 10 14; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

        VBox totalsBox = new VBox(4);
        totalsBox.setAlignment(Pos.CENTER_RIGHT);

        Label subLbl = new Label(String.format("Subtotal: LKR %,.2f", selected.getSubTotal() != null ? selected.getSubTotal() : 0.0));
        subLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        Label discLbl = new Label(String.format("Discount: - LKR %,.2f", selected.getDiscount() != null ? selected.getDiscount() : 0.0));
        discLbl.setStyle("-fx-text-fill: #d97706; -fx-font-size: 12px;");

        Label netLbl = new Label(String.format("NET TOTAL: LKR %,.2f", selected.getTotalCost() != null ? selected.getTotalCost() : 0.0));
        netLbl.setStyle("-fx-text-fill: #059669; -fx-font-size: 16px; -fx-font-weight: bold;");

        totalsBox.getChildren().addAll(subLbl, discLbl, netLbl);
        bottomBar.getChildren().add(totalsBox);

        contentBox.getChildren().addAll(headerCard, table, bottomBar);
        dialog.getDialogPane().setContent(contentBox);

        ButtonType reprintBtn = new ButtonType("Print Thermal Receipt", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(reprintBtn, ButtonType.CLOSE);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == reprintBtn) {
            btnReprintReceiptOnAction(null);
        }
    }

    private Label createMetaLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold;");
        return lbl;
    }

    private Label createValueLabel(String text) {
        Label lbl = new Label(text != null ? text : "-");
        lbl.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px;");
        return lbl;
    }

    private TableCell<OrderDetail, Double> createDetailCurrencyCell() {
        return new TableCell<OrderDetail, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) setText(null);
                else setText(String.format("LKR %,.2f", amount));
            }
        };
    }

    @FXML
    void btnReprintReceiptOnAction(ActionEvent event) {
        Order selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an order to reprint receipt.");
            return;
        }

        Order fullOrder = orderService.getOrder(selected.getOrderId());
        if (fullOrder == null) fullOrder = selected;

        Dialog<ButtonType> receiptDialog = new Dialog<>();
        receiptDialog.setTitle("Reprint Receipt • " + fullOrder.getOrderId());
        receiptDialog.setHeaderText("Clothify Thermal Receipt");

        TextArea area = new TextArea(ReceiptGenerator.generateFormattedTextReceipt(fullOrder));
        area.setEditable(false);
        area.setPrefRowCount(22);
        area.setPrefColumnCount(45);
        area.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");

        ButtonType copyBtn = new ButtonType("📋 Copy to Clipboard", ButtonBar.ButtonData.OTHER);
        ButtonType printBtn = new ButtonType("Export / Save to File", ButtonBar.ButtonData.OK_DONE);
        receiptDialog.getDialogPane().getButtonTypes().addAll(copyBtn, printBtn, ButtonType.CLOSE);
        receiptDialog.getDialogPane().setContent(area);

        receiptDialog.showAndWait().ifPresent(btn -> {
            if (btn == copyBtn) {
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(area.getText());
                javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                AlertUtil.showSuccess("Copied", "Thermal receipt copied to system clipboard!");
            } else if (btn == printBtn) {
                File file = ReceiptGenerator.saveReceiptToFile(selected);
                AlertUtil.showSuccess("Receipt Exported", "Receipt saved to: " + file.getAbsolutePath());
            }
        });
    }

    @FXML
    void btnProcessReturnOnAction(ActionEvent event) {
        Order selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an order to process a return.");
            return;
        }

        List<OrderDetail> details = orderService.getOrderDetails(selected.getOrderId());
        if (details.isEmpty()) {
            AlertUtil.showWarning("No Items", "This order contains no returnable items.");
            return;
        }

        Dialog<OrderReturn> dialog = new Dialog<>();
        dialog.setTitle("Process Order Return & Restock");
        dialog.setHeaderText("Order #" + selected.getOrderId() + " - Return Item & Issue Refund");

        ButtonType confirmReturn = new ButtonType("Process Return & Restock", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmReturn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 40, 10, 10));

        ComboBox<OrderDetail> cmbItems = new ComboBox<>();
        cmbItems.setItems(FXCollections.observableArrayList(details));
        cmbItems.setCellFactory(lv -> new ListCell<OrderDetail>() {
            @Override
            protected void updateItem(OrderDetail item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getItemCode() + " - " + item.getItemName() + " (Qty bought: " + item.getQty() + ")");
            }
        });
        cmbItems.setButtonCell(new ListCell<OrderDetail>() {
            @Override
            protected void updateItem(OrderDetail item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getItemCode() + " - " + item.getItemName());
            }
        });
        if (!details.isEmpty()) cmbItems.setValue(details.get(0));

        TextField txtReturnQty = new TextField("1");
        TextField txtReason = new TextField("Customer size exchange / defective");
        Label lblRefundPreview = new Label("LKR 0.00");
        lblRefundPreview.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");

        Runnable updateRefund = () -> {
            OrderDetail d = cmbItems.getValue();
            if (d != null) {
                try {
                    int q = Integer.parseInt(txtReturnQty.getText().trim());
                    double refund = d.getUnitPrice() * q;
                    lblRefundPreview.setText(String.format("LKR %,.2f", refund));
                } catch (NumberFormatException e) {
                    lblRefundPreview.setText("LKR 0.00");
                }
            }
        };

        cmbItems.setOnAction(e -> updateRefund.run());
        txtReturnQty.setOnKeyReleased(e -> updateRefund.run());
        updateRefund.run();

        grid.add(new Label("Select Item to Return:"), 0, 0);
        grid.add(cmbItems, 1, 0);
        grid.add(new Label("Return Quantity:"), 0, 1);
        grid.add(txtReturnQty, 1, 1);
        grid.add(new Label("Reason for Return:"), 0, 2);
        grid.add(txtReason, 1, 2);
        grid.add(new Label("Estimated Refund:"), 0, 3);
        grid.add(lblRefundPreview, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == confirmReturn) {
                OrderDetail d = cmbItems.getValue();
                if (d == null) return null;
                int q = 1;
                try {
                    q = Integer.parseInt(txtReturnQty.getText().trim());
                    if (q <= 0 || q > d.getQty()) {
                        AlertUtil.showError("Invalid Quantity", "Return quantity must be between 1 and " + d.getQty());
                        return null;
                    }
                } catch (NumberFormatException e) {
                    AlertUtil.showError("Invalid Input", "Please enter a valid numeric quantity.");
                    return null;
                }
                double refundAmt = d.getUnitPrice() * q;
                return new OrderReturn(
                        IdGenerator.generateReturnId(),
                        selected.getOrderId(),
                        d.getItemCode(),
                        q,
                        txtReason.getText().trim(),
                        refundAmt,
                        LocalDate.now()
                );
            }
            return null;
        });

        Optional<OrderReturn> ret = dialog.showAndWait();
        ret.ifPresent(r -> {
            boolean done = orderService.processReturn(r);
            if (done) {
                AlertUtil.showSuccess("Return Processed", "Item successfully restocked to inventory!\nRefund issued: LKR " + r.getRefundAmount());
                loadOrders();
            } else {
                AlertUtil.showError("Return Failed", "Could not process return. Please check database logs.");
            }
        });
    }
}
