package controller.order;

import controller.item.ItemController;
import controller.item.ItemService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.CartItem;
import model.Item;
import model.Order;
import model.OrderDetail;
import security.UserSession;
import util.AlertUtil;
import util.IdGenerator;
import util.ReceiptGenerator;
import util.ValidationUtil;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PosOrderController implements Initializable {

    @FXML
    private ComboBox<String> cmbCategoryFilter;

    @FXML
    private TextField txtSearchProduct;

    @FXML
    private TableView<Item> tblCatalog;

    @FXML
    private TableColumn<Item, String> colCatCode;

    @FXML
    private TableColumn<Item, String> colCatName;

    @FXML
    private TableColumn<Item, String> colCatCategory;

    @FXML
    private TableColumn<Item, String> colCatSize;

    @FXML
    private TableColumn<Item, Double> colCatPrice;

    @FXML
    private TableColumn<Item, Integer> colCatStock;

    @FXML
    private Label lblSelectedItemSummary;

    @FXML
    private TextField txtAddQty;

    @FXML
    private Label lblOrderId;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtCustomerPhone;

    @FXML
    private TableView<CartItem> tblCart;

    @FXML
    private TableColumn<CartItem, String> colCartName;

    @FXML
    private TableColumn<CartItem, Double> colCartPrice;

    @FXML
    private TableColumn<CartItem, Integer> colCartQty;

    @FXML
    private TableColumn<CartItem, Double> colCartTotal;

    @FXML
    private Label lblSubTotal;

    @FXML
    private TextField txtDiscountPercent;

    @FXML
    private Label lblDiscountAmount;

    @FXML
    private Label lblNetTotal;

    @FXML
    private ComboBox<String> cmbPaymentType;

    @FXML
    private TextField txtAmountTendered;

    @FXML
    private Label lblChangeAmount;

    private final ItemService itemService = ItemController.getInstance();
    private final OrderService orderService = OrderController.getInstance();

    private final ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private ObservableList<Item> catalogMasterList = FXCollections.observableArrayList();

    private String currentOrderId;
    private Item lastSelectedItem = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCatalogTable();
        initCartTable();
        initCombos();
        loadCatalog();
        resetNewOrder();

        if (txtSearchProduct != null) {
            txtSearchProduct.textProperty().addListener((obs, oldVal, newVal) -> filterCatalog());
        }
    }

    private void initCatalogTable() {
        colCatCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colCatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCatSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colCatPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        colCatCode.setStyle("-fx-alignment: CENTER;");
        colCatSize.setStyle("-fx-alignment: CENTER;");
        colCatStock.setStyle("-fx-alignment: CENTER;");
        colCatPrice.setStyle("-fx-alignment: CENTER-RIGHT;");

        colCatPrice.setCellFactory(col -> new TableCell<Item, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("%,.2f", price));
            }
        });

        Label catPlaceholder = new Label("No boutique apparel items found");
        catPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        tblCatalog.setPlaceholder(catPlaceholder);

        tblCatalog.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lastSelectedItem = newVal;
                updateSelectionBadge(newVal);
            }
        });
    }

    private void updateSelectionBadge(Item item) {
        if (lblSelectedItemSummary == null) return;
        if (item != null) {
            double price = item.getPrice() != null ? item.getPrice() : 0.0;
            int stock = item.getQuantity() != null ? item.getQuantity() : 0;
            lblSelectedItemSummary.setText(String.format("👗 Selected: %s - %s  (LKR %,.2f  |  In Stock: %d)",
                    item.getItemCode(), item.getName(), price, stock));
            lblSelectedItemSummary.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold;");
        } else {
            lblSelectedItemSummary.setText("👗 Click any product above to select");
            lblSelectedItemSummary.setStyle("-fx-text-fill: #475569; -fx-font-weight: bold;");
        }
    }

    private void initCartTable() {
        colCartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCartPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        colCartQty.setStyle("-fx-alignment: CENTER;");
        colCartPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCartTotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        colCartPrice.setCellFactory(col -> new TableCell<CartItem, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("%,.2f", price));
            }
        });

        colCartTotal.setCellFactory(col -> new TableCell<CartItem, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) setText(null);
                else setText(String.format("%,.2f", total));
            }
        });

        Label cartPlaceholder = new Label("Shopping cart is empty\nSelect an item from the catalog to add");
        cartPlaceholder.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        cartPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        tblCart.setPlaceholder(cartPlaceholder);

        tblCart.setItems(cartList);
    }

    private void initCombos() {
        cmbCategoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "Gents", "Ladies", "Kids", "Accessories", "Footwear", "Winter"
        ));
        cmbCategoryFilter.setValue("All Categories");

        cmbPaymentType.setItems(FXCollections.observableArrayList("Cash", "Card", "Online"));
        cmbPaymentType.setValue("Cash");
    }

    public void loadCatalog() {
        catalogMasterList = itemService.getAllItems();
        tblCatalog.setItems(catalogMasterList);
    }

    private void resetNewOrder() {
        currentOrderId = IdGenerator.generateOrderId();
        lblOrderId.setText("Order #" + currentOrderId);
        cartList.clear();
        txtCustomerName.clear();
        txtCustomerPhone.clear();
        txtDiscountPercent.setText("0");
        txtAmountTendered.clear();
        lblChangeAmount.setText("LKR 0.00");
        recalculateTotals();
    }

    @FXML
    void onCategoryFilterChanged(ActionEvent event) {
        filterCatalog();
    }

    private void filterCatalog() {
        String selected = cmbCategoryFilter != null ? cmbCategoryFilter.getValue() : "All Categories";
        String query = (txtSearchProduct != null && txtSearchProduct.getText() != null) ? txtSearchProduct.getText().trim().toLowerCase() : "";

        ObservableList<Item> filtered = FXCollections.observableArrayList();
        for (Item item : catalogMasterList) {
            boolean matchCategory = (selected == null || "All Categories".equals(selected) || selected.equalsIgnoreCase(item.getCategory()));
            boolean matchQuery = query.isEmpty() ||
                    (item.getItemCode() != null && item.getItemCode().toLowerCase().contains(query)) ||
                    (item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                    (item.getCategory() != null && item.getCategory().toLowerCase().contains(query)) ||
                    (item.getSize() != null && item.getSize().toLowerCase().contains(query));

            if (matchCategory && matchQuery) {
                filtered.add(item);
            }
        }
        tblCatalog.setItems(filtered);
        if (!filtered.isEmpty()) {
            tblCatalog.getSelectionModel().selectFirst();
            lastSelectedItem = filtered.get(0);
            updateSelectionBadge(lastSelectedItem);
        }
    }

    @FXML
    void onSearchEnter(ActionEvent event) {
        btnAddSelectedItemToCart(event);
    }

    @FXML
    void btnAddToCartFromSearch(ActionEvent event) {
        btnAddSelectedItemToCart(event);
    }

    @FXML
    void btnAddSelectedItemToCart(ActionEvent event) {
        Item target = tblCatalog.getSelectionModel().getSelectedItem();
        if (target == null) {
            target = lastSelectedItem;
        }

        String query = (txtSearchProduct != null && txtSearchProduct.getText() != null) ? txtSearchProduct.getText().trim().toLowerCase() : "";
        if (target == null && !query.isEmpty()) {
            for (Item item : catalogMasterList) {
                if (item.getItemCode().equalsIgnoreCase(query) || item.getName().toLowerCase().contains(query)) {
                    target = item;
                    break;
                }
            }
        }

        if (target == null && !tblCatalog.getItems().isEmpty()) {
            target = tblCatalog.getItems().get(0);
        }

        if (target != null) {
            addItemToCart(target);
            return;
        }

        AlertUtil.showWarning("No Product Selected", "Please select a product from the table or type in the search bar.");
    }

    @FXML
    void onCatalogRowClicked(MouseEvent event) {
        Item selected = tblCatalog.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lastSelectedItem = selected;
            updateSelectionBadge(selected);
            if (event.getClickCount() == 2) {
                addItemToCart(selected);
            }
        }
    }

    private void addItemToCart(Item item) {
        if (item == null) {
            AlertUtil.showWarning("No Product Selected", "Please select a product to add.");
            return;
        }

        int qtyToAdd = 1;
        try {
            if (txtAddQty != null && txtAddQty.getText() != null) {
                qtyToAdd = Integer.parseInt(txtAddQty.getText().trim());
            }
            if (qtyToAdd <= 0) qtyToAdd = 1;
        } catch (NumberFormatException ignored) {
            qtyToAdd = 1;
        }

        int availStock = item.getQuantity() != null ? item.getQuantity() : 0;
        double unitPrice = item.getPrice() != null ? item.getPrice() : 0.0;

        if (availStock <= 0) {
            AlertUtil.showWarning("Out of Stock", "Item '" + item.getName() + "' is completely out of stock!");
            return;
        }

        // Check if already in cart
        CartItem existing = null;
        for (CartItem ci : cartList) {
            if (ci.getItemCode().equals(item.getItemCode())) {
                existing = ci;
                break;
            }
        }

        if (existing != null) {
            int newTotalQty = existing.getQuantity() + qtyToAdd;
            if (newTotalQty > availStock) {
                AlertUtil.showWarning("Stock Limit", "Only " + availStock + " units available in inventory (already " + existing.getQuantity() + " in cart).");
                return;
            }
            existing.updateQuantity(newTotalQty);
            tblCart.refresh();
            tblCart.getSelectionModel().select(existing);
            tblCart.scrollTo(existing);
        } else {
            if (qtyToAdd > availStock) {
                AlertUtil.showWarning("Stock Limit", "Only " + availStock + " units available in inventory.");
                return;
            }
            CartItem newItem = new CartItem(
                    item.getItemCode(),
                    item.getName(),
                    item.getCategory() != null ? item.getCategory() : "Apparel",
                    item.getSize() != null ? item.getSize() : "-",
                    unitPrice,
                    qtyToAdd,
                    unitPrice * qtyToAdd,
                    availStock
            );
            cartList.add(newItem);
            tblCart.getSelectionModel().select(newItem);
            tblCart.scrollTo(newItem);
        }

        recalculateTotals();

        // Immediate tactile success notification in badge strip
        if (lblSelectedItemSummary != null) {
            lblSelectedItemSummary.setText(String.format("✨ Added %dx '%s' to Cart! (Subtotal: %s)",
                    qtyToAdd, item.getName(), lblSubTotal != null ? lblSubTotal.getText() : ""));
            lblSelectedItemSummary.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
        }
    }

    @FXML
    void btnQuickAddOne(ActionEvent event) { txtAddQty.setText("1"); }
    @FXML
    void btnQuickAddTwo(ActionEvent event) { txtAddQty.setText("2"); }
    @FXML
    void btnQuickAddFive(ActionEvent event) { txtAddQty.setText("5"); }

    @FXML
    void btnRemoveCartItemOnAction(ActionEvent event) {
        CartItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartList.remove(selected);
            recalculateTotals();
        }
    }

    @FXML
    void btnClearCartOnAction(ActionEvent event) {
        if (!cartList.isEmpty()) {
            boolean confirm = AlertUtil.showConfirmation("Clear Cart", "Clear current shopping cart?", "This will remove all items in the current transaction.");
            if (confirm) {
                cartList.clear();
                recalculateTotals();
            }
        }
    }

    @FXML
    void onDiscountChanged(KeyEvent event) {
        recalculateTotals();
    }

    @FXML
    void onTenderedChanged(KeyEvent event) {
        recalculateChange();
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartItem ci : cartList) {
            subtotal += ci.getTotal();
        }
        return subtotal;
    }

    private void recalculateTotals() {
        double subtotal = calculateSubtotal();
        lblSubTotal.setText(String.format("LKR %,.2f", subtotal));

        double discountPct = 0.0;
        try {
            discountPct = Double.parseDouble(txtDiscountPercent.getText().trim());
            if (discountPct < 0) discountPct = 0;
            if (discountPct > 100) discountPct = 100;
        } catch (NumberFormatException ignored) {}

        double discountAmount = (subtotal * discountPct) / 100.0;
        lblDiscountAmount.setText(String.format("- LKR %,.2f", discountAmount));

        double netTotal = subtotal - discountAmount;
        lblNetTotal.setText(String.format("LKR %,.2f", netTotal));

        recalculateChange();
    }

    private void recalculateChange() {
        double subtotal = calculateSubtotal();
        double discountPct = 0.0;
        try {
            discountPct = Double.parseDouble(txtDiscountPercent.getText().trim());
        } catch (Exception ignored) {}
        double netTotal = subtotal - (subtotal * discountPct / 100.0);

        try {
            double tendered = Double.parseDouble(txtAmountTendered.getText().trim());
            double change = tendered - netTotal;
            if (change >= 0) {
                lblChangeAmount.setText(String.format("LKR %,.2f", change));
            } else {
                lblChangeAmount.setText(String.format("Due: LKR %,.2f", Math.abs(change)));
            }
        } catch (NumberFormatException e) {
            lblChangeAmount.setText("LKR 0.00");
        }
    }

    @FXML
    void btnCompleteOrderOnAction(ActionEvent event) {
        if (cartList.isEmpty()) {
            AlertUtil.showWarning("Empty Cart", "Please add at least one item to cart before checkout.");
            return;
        }

        double subtotal = calculateSubtotal();
        double discountPct = 0.0;
        try {
            discountPct = Double.parseDouble(txtDiscountPercent.getText().trim());
        } catch (Exception ignored) {}
        double discountAmount = (subtotal * discountPct) / 100.0;
        double netTotal = subtotal - discountAmount;

        String paymentType = cmbPaymentType.getValue();
        if ("Cash".equalsIgnoreCase(paymentType) && ValidationUtil.isNotEmpty(txtAmountTendered.getText())) {
            try {
                double tendered = Double.parseDouble(txtAmountTendered.getText().trim());
                if (tendered < 0) {
                    AlertUtil.showError("Invalid Payment", "Amount tendered cannot be negative.");
                    return;
                }
                if (tendered < netTotal) {
                    AlertUtil.showError("Insufficient Payment", String.format("Amount tendered (LKR %,.2f) is less than net total (LKR %,.2f).", tendered, netTotal));
                    return;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showError("Invalid Input", "Please enter a valid numeric tendered amount.");
                return;
            }
        }

        String cashierName = UserSession.getInstance().getUserDisplayName();
        String customer = txtCustomerName.getText() != null && !txtCustomerName.getText().trim().isEmpty() ? txtCustomerName.getText().trim() : "Walk-in Customer";
        String phone = txtCustomerPhone.getText() != null ? txtCustomerPhone.getText().trim() : "";

        Order order = new Order(
                currentOrderId,
                LocalDate.now(),
                LocalTime.now(),
                customer,
                phone.contains("@") ? phone : "",
                !phone.contains("@") ? phone : "",
                paymentType,
                subtotal,
                discountAmount,
                netTotal,
                "EMP-01",
                cashierName,
                "COMPLETED"
        );

        List<OrderDetail> details = new ArrayList<>();
        for (CartItem ci : cartList) {
            details.add(new OrderDetail(
                    currentOrderId,
                    ci.getItemCode(),
                    ci.getName(),
                    ci.getUnitPrice(),
                    ci.getQuantity(),
                    ci.getTotal()
            ));
        }
        order.setOrderDetails(details);

        // Atomic transaction checkout
        boolean success = orderService.placeOrder(order);
        if (success) {
            // Show modern receipt modal
            showReceiptModal(order);
            // Refresh inventory catalog
            loadCatalog();
            resetNewOrder();
        } else {
            AlertUtil.showError("Transaction Failed", "Could not complete order. Please verify stock availability.");
        }
    }

    @FXML
    void btnViewOrderHistoryOnAction(ActionEvent event) {
        openOrderHistory(null);
    }

    public void openOrderHistory(String selectOrderId) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/order_history_view.fxml"));
            javafx.scene.Parent root = loader.load();
            OrderHistoryController controller = loader.getController();
            if (selectOrderId != null && controller != null) {
                controller.focusOrder(selectOrderId);
            }

            javafx.stage.Stage stage = new javafx.stage.Stage();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setTitle("Clothify - Order Records & Transaction History");
            util.WindowUtil.centerInVisualBounds(stage, 1120, 720);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh catalog in case returns or adjustments were made
            loadCatalog();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error", "Could not load Order History: " + e.getMessage());
        }
    }

    private void showReceiptModal(Order order) {
        Dialog<ButtonType> receiptDialog = new Dialog<>();
        receiptDialog.setTitle("Invoice Receipt • " + order.getOrderId());
        receiptDialog.setHeaderText("Order Completed Successfully!");

        TextArea area = new TextArea(ReceiptGenerator.generateFormattedTextReceipt(order));
        area.setEditable(false);
        area.setPrefRowCount(22);
        area.setPrefColumnCount(45);
        area.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");

        ButtonType copyBtn = new ButtonType("📋 Copy to Clipboard", ButtonBar.ButtonData.OTHER);
        ButtonType printBtn = new ButtonType("💾 Print / Save to File", ButtonBar.ButtonData.OK_DONE);
        ButtonType viewOrdersBtn = new ButtonType("📜 View Placed Orders", ButtonBar.ButtonData.HELP_2);
        receiptDialog.getDialogPane().getButtonTypes().addAll(copyBtn, printBtn, viewOrdersBtn, ButtonType.CLOSE);
        receiptDialog.getDialogPane().setContent(area);

        receiptDialog.showAndWait().ifPresent(btn -> {
            if (btn == copyBtn) {
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(area.getText());
                javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                AlertUtil.showSuccess("Copied", "Thermal receipt copied to system clipboard!");
            } else if (btn == printBtn) {
                File file = ReceiptGenerator.saveReceiptToFile(order);
                AlertUtil.showSuccess("Receipt Saved", "Receipt written to: " + file.getAbsolutePath());
            } else if (btn == viewOrdersBtn) {
                openOrderHistory(order.getOrderId());
            }
        });
    }
}
