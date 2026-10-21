package controller.dashboard;

import controller.employee.EmployeeController;
import controller.item.ItemController;
import controller.order.OrderController;
import controller.report.ReportController;
import controller.supplier.SupplierController;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.DashboardSummary;
import model.Item;
import model.User;
import security.UserSession;
import util.AlertUtil;
import util.WindowUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DashboardFormController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView bgImageView;

    @FXML
    private Label lblGreeting;

    @FXML
    private Label lblClock;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblUserInitial;

    @FXML
    private Label lblUserName;

    @FXML
    private Label lblUserRole;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private Label lblLowStock;

    @FXML
    private Label lblStockOut;

    @FXML
    private Label lblStockoutListBadge;

    @FXML
    private VBox vboxStockoutList;

    @FXML
    private Label lblTotalSuppliers;

    @FXML
    private Label lblTodayRevenue;

    @FXML
    private Label lblTotalOrders;

    @FXML
    private Label lblTotalEmployees;

    @FXML
    private Group svgWheelGroup;

    @FXML
    private StackPane centerOrbPane;

    @FXML
    private VBox orbIdleBox;

    @FXML
    private ImageView imgCenterLogo;

    @FXML
    private VBox orbHoverBox;

    @FXML
    private Label lblOrbIcon;

    @FXML
    private Label lblOrbTitle;

    @FXML
    private Label lblOrbSubtitle;

    @FXML
    private Group curvedLabelsGroup;

    private Timeline clockTimeline;
    private Runnable currentOrbAction = null;
    private NavModule activeHoveredModule = null;
    private FadeTransition currentOrbFade = null;
    private long lastNavClickTime = 0;

    public static class NavModule {
        final String key;
        final String label;
        final String icon;
        final String title;
        final String subtitle;
        final String styleClass;
        final Runnable clickAction;

        public NavModule(String key, String label, String icon, String title, String subtitle, String styleClass, Runnable clickAction) {
            this.key = key;
            this.label = label;
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.styleClass = styleClass;
            this.clickAction = clickAction;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initResponsiveLayout();
        initUserProfile();
        initLiveClock();
        loadDatabaseMetrics();
        initCenterLogo();
        initDynamicWheel();
        initWindowCloseHandler();
    }

    private void initWindowCloseHandler() {
        javafx.application.Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setOnCloseRequest(e -> {
                    if (clockTimeline != null) {
                        clockTimeline.stop();
                    }
                    UserSession.getInstance().clearSession();
                });
            }
        });
    }

    private void initResponsiveLayout() {
        if (bgImageView != null && rootPane != null) {
            bgImageView.fitWidthProperty().bind(rootPane.widthProperty());
            bgImageView.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    private void initUserProfile() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getEmail();
            if (lblUserName != null) lblUserName.setText(name);
            if (lblUserInitial != null) lblUserInitial.setText(name.substring(0, 1).toUpperCase());
            String role = user.getRole() != null ? user.getRole().toUpperCase() : "CASHIER";
            if (lblUserRole != null) lblUserRole.setText(role);
        } else {
            if (lblUserName != null) lblUserName.setText("Sarah Jenkins");
            if (lblUserInitial != null) lblUserInitial.setText("S");
            if (lblUserRole != null) lblUserRole.setText("CASHIER");
        }
    }

    private void initLiveClock() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy");

        LocalDateTime initialNow = LocalDateTime.now();
        if (lblClock != null) lblClock.setText(initialNow.format(timeFormatter));
        if (lblDate != null) lblDate.setText(initialNow.format(dateFormatter));
        updateGreeting(initialNow);

        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            if (lblClock != null) lblClock.setText(now.format(timeFormatter));
            if (lblDate != null) lblDate.setText(now.format(dateFormatter));
            updateGreeting(now);
        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void updateGreeting(LocalDateTime now) {
        if (lblGreeting == null) return;
        int hour = now.getHour();
        String greet = hour < 12 ? "Good morning" : (hour < 17 ? "Good afternoon" : "Good evening");
        String name = (lblUserName != null && !lblUserName.getText().isEmpty()) ? lblUserName.getText() : "Staff";
        lblGreeting.setText(greet + ", " + name + "!");
    }

    public void loadDatabaseMetrics() {
        try {
            // Products & Stock (Track Low Stock and Stock Out separately)
            var items = ItemController.getInstance().getAllItems();
            int totalItems = items != null ? items.size() : 0;
            int lowStockCount = 0;
            int stockOutCount = 0;
            List<Item> stockOutItems = new ArrayList<>();
            List<Item> lowStockItems = new ArrayList<>();

            if (items != null) {
                for (Item it : items) {
                    if (it.getQuantity() != null) {
                        if (it.getQuantity() <= 0) {
                            stockOutCount++;
                            stockOutItems.add(it);
                        } else if (it.getQuantity() <= 5) {
                            lowStockCount++;
                            lowStockItems.add(it);
                        }
                    }
                }
            }
            if (lblTotalProducts != null) lblTotalProducts.setText(totalItems + " Items");
            if (lblLowStock != null) lblLowStock.setText(lowStockCount + " Low");
            if (lblStockOut != null) lblStockOut.setText(stockOutCount + " Out");

            // Populate the Stockout mini-list in the left boutique card
            if (vboxStockoutList != null) {
                final int finalStockOutCount = stockOutCount;
                Runnable updateList = () -> {
                    vboxStockoutList.getChildren().clear();
                    if (stockOutItems.isEmpty()) {
                        HBox emptyRow = new HBox(6);
                        emptyRow.setAlignment(Pos.CENTER_LEFT);
                        emptyRow.setStyle("-fx-padding: 6 8; -fx-background-color: #ecfdf5; -fx-background-radius: 6; -fx-border-color: #a7f3d0; -fx-border-radius: 6;");
                        Label checkLbl = new Label("✓ No stockouts in catalog");
                        checkLbl.setStyle("-fx-text-fill: #047857; -fx-font-size: 10px; -fx-font-weight: bold;");
                        emptyRow.getChildren().add(checkLbl);
                        vboxStockoutList.getChildren().add(emptyRow);

                        if (lblStockoutListBadge != null) {
                            lblStockoutListBadge.setText("0 Out");
                            lblStockoutListBadge.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-padding: 1 6; -fx-background-radius: 8; -fx-font-size: 9px; -fx-font-weight: bold;");
                        }
                    } else {
                        if (lblStockoutListBadge != null) {
                            lblStockoutListBadge.setText(finalStockOutCount + " Out");
                            lblStockoutListBadge.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-padding: 1 6; -fx-background-radius: 8; -fx-font-size: 9px; -fx-font-weight: bold;");
                        }

                        for (Item it : stockOutItems) {
                            HBox row = new HBox(6);
                            row.setAlignment(Pos.CENTER_LEFT);
                            row.setStyle("-fx-background-color: #ffffff; -fx-padding: 4 6; -fx-background-radius: 6; -fx-border-color: #fecaca; -fx-border-radius: 6;");

                            Label codeBadge = new Label(it.getItemCode());
                            codeBadge.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-background-color: #fef2f2; -fx-padding: 1 4; -fx-background-radius: 4; -fx-border-color: #fca5a5; -fx-border-radius: 4;");

                            Label nameLbl = new Label(it.getName());
                            nameLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #0f172a; -fx-font-weight: bold;");
                            HBox.setHgrow(nameLbl, Priority.ALWAYS);
                            nameLbl.setMaxWidth(110.0);
                            nameLbl.setTextOverrun(OverrunStyle.ELLIPSIS);

                            Label qtyBadge = new Label("0 Qty");
                            qtyBadge.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

                            row.getChildren().addAll(codeBadge, nameLbl, qtyBadge);
                            vboxStockoutList.getChildren().add(row);
                        }
                    }
                };

                if (javafx.application.Platform.isFxApplicationThread()) {
                    updateList.run();
                } else {
                    javafx.application.Platform.runLater(updateList);
                }
            }

            // Suppliers
            var suppliers = SupplierController.getInstance().getAllSuppliers();
            int supCount = suppliers != null ? suppliers.size() : 0;
            if (lblTotalSuppliers != null) lblTotalSuppliers.setText(supCount + " Partners");

            // Employees
            var employees = EmployeeController.getInstance().getAllEmployees();
            int empCount = employees != null ? employees.size() : 0;
            if (lblTotalEmployees != null) lblTotalEmployees.setText(empCount + " Staff");

            // Orders & Revenue
            DashboardSummary summary = ReportController.getInstance().getDashboardSummary();
            if (summary != null) {
                if (lblTodayRevenue != null && summary.getTodaySales() != null) {
                    lblTodayRevenue.setText(String.format("LKR %,.2f", summary.getTodaySales()));
                }
                if (lblTotalOrders != null && summary.getTodayOrdersCount() != null) {
                    lblTotalOrders.setText(summary.getTodayOrdersCount() + " Orders");
                }
            } else {
                var orders = OrderController.getInstance().getAllOrders();
                int orderCount = orders != null ? orders.size() : 0;
                if (lblTotalOrders != null) lblTotalOrders.setText(orderCount + " Orders");
                if (lblTodayRevenue != null) lblTodayRevenue.setText("LKR 0.00");
            }
        } catch (Exception e) {
            System.err.println("Metric load note: " + e.getMessage());
        }
    }

    @FXML
    void btnRefreshStats(ActionEvent event) {
        loadDatabaseMetrics();
        initDynamicWheel();
        AlertUtil.showSuccess("Data Refreshed", "All dashboard metrics and stock indicators updated.");
    }

    private void initCenterLogo() {
        if (imgCenterLogo != null) {
            Circle clip = new Circle(140.0, 140.0, 140.0);
            imgCenterLogo.setClip(clip);
        }
        if (centerOrbPane != null) {
            Circle orbClip = new Circle(146.0, 146.0, 146.0);
            centerOrbPane.setClip(orbClip);
            centerOrbPane.setPickOnBounds(false);
            centerOrbPane.setCursor(Cursor.HAND);

            EventHandler<MouseEvent> orbClickHandler = e -> {
                if (e.getButton() == MouseButton.PRIMARY || e.getButton() == null) {
                    Runnable act = (currentOrbAction != null) ? currentOrbAction :
                            (activeHoveredModule != null ? activeHoveredModule.clickAction : null);
                    dispatchModuleAction(act);
                }
            };

            centerOrbPane.setOnMouseClicked(orbClickHandler);
            centerOrbPane.setOnMouseReleased(orbClickHandler);

            if (orbHoverBox != null) {
                orbHoverBox.setOnMouseClicked(orbClickHandler);
                orbHoverBox.setOnMouseReleased(orbClickHandler);
                orbHoverBox.setCursor(Cursor.HAND);
                for (Node child : orbHoverBox.getChildren()) {
                    child.setOnMouseClicked(orbClickHandler);
                    child.setOnMouseReleased(orbClickHandler);
                    child.setCursor(Cursor.HAND);
                }
            }

            if (centerOrbPane.getParent() != null) {
                centerOrbPane.getParent().setOnMouseExited(e -> {
                    if (e.getTarget() == centerOrbPane.getParent()) {
                        hideOrbHoverInfo();
                        activeHoveredModule = null;
                        currentOrbAction = null;
                    }
                });
            }
        }
    }

    private void dispatchModuleAction(Runnable action) {
        if (action == null) return;
        long now = System.currentTimeMillis();
        if (now - lastNavClickTime < 500) {
            return;
        }
        lastNavClickTime = now;
        action.run();
    }

    // =========================================================================
    // ROLE-BASED DYNAMIC RADIAL NAVIGATION WHEEL (CASHIER 3 BY DEFAULT, UP TO 6)
    // =========================================================================

    public void initDynamicWheel() {
        if (svgWheelGroup == null || curvedLabelsGroup == null) {
            return;
        }

        svgWheelGroup.getChildren().clear();
        curvedLabelsGroup.getChildren().clear();
        curvedLabelsGroup.setMouseTransparent(true);
        curvedLabelsGroup.setPickOnBounds(false);

        User user = UserSession.getInstance().getCurrentUser();

        List<NavModule> activeModules = new ArrayList<>();

        // 1. ORDER (Always available to Cashier & Admin)
        if (hasAccess(user, "ORDER")) {
            activeModules.add(new NavModule(
                    "ORDER", "Order Management", "🛍️",
                    "POINT OF SALE (POS)", "Fast Checkout & Invoicing",
                    "order-sector", () -> btnOrderManagementOnAction(null)
            ));
        }

        // 2. PRODUCT (Always available to Cashier & Admin)
        if (hasAccess(user, "PRODUCT")) {
            String count = (lblTotalProducts != null && !lblTotalProducts.getText().isEmpty()) ? lblTotalProducts.getText() : "Catalog";
            activeModules.add(new NavModule(
                    "PRODUCT", "Product Management", "👗",
                    "PRODUCT CATALOG", count + " in Stock",
                    "product-sector", () -> btnProductManagementOnAction(null)
            ));
        }

        // 3. EMPLOYEE (Admin, or Cashier granted Employee permission)
        if (hasAccess(user, "EMPLOYEE")) {
            String count = (lblTotalEmployees != null && !lblTotalEmployees.getText().isEmpty()) ? lblTotalEmployees.getText() : "Staff";
            activeModules.add(new NavModule(
                    "EMPLOYEE", "Employee Management", "👥",
                    "EMPLOYEE ROSTER", count + " on Duty",
                    "employee-sector", () -> btnEmployeeManagementOnAction(null)
            ));
        }

        // 4. REPORT (Always available to Cashier & Admin)
        if (hasAccess(user, "REPORT")) {
            activeModules.add(new NavModule(
                    "REPORT", "Report Management", "📈",
                    "REPORT & ANALYTICS", "Financial & Inventory Breakdown",
                    "report-sector", () -> btnReportManagementOnAction(null)
            ));
        }

        // 5. SUPPLIER (Admin, or Cashier granted Supplier permission)
        if (hasAccess(user, "SUPPLIER")) {
            String count = (lblTotalSuppliers != null && !lblTotalSuppliers.getText().isEmpty()) ? lblTotalSuppliers.getText() : "Partners";
            activeModules.add(new NavModule(
                    "SUPPLIER", "Supplier Management", "🚚",
                    "SUPPLIER NETWORK", count + " Active",
                    "supplier-sector", () -> btnSupplierManagementOnAction(null)
            ));
        }

        // 6. USER (Admin, or Cashier granted User permission)
        if (hasAccess(user, "USER")) {
            activeModules.add(new NavModule(
                    "USER", "User Management", "🛡️",
                    "USER SECURITY", "Accounts, Roles & Permissions",
                    "user-sector", () -> btnUserManagementOnAction(null)
            ));
        }

        // Ensure at least core 3 are present if list is empty
        if (activeModules.isEmpty()) {
            activeModules.add(new NavModule("ORDER", "Order Management", "🛍️", "POINT OF SALE (POS)", "Fast Checkout & Invoicing", "order-sector", () -> btnOrderManagementOnAction(null)));
            activeModules.add(new NavModule("PRODUCT", "Product Management", "👗", "PRODUCT CATALOG", "Apparel in Stock", "product-sector", () -> btnProductManagementOnAction(null)));
            activeModules.add(new NavModule("REPORT", "Report Management", "📈", "REPORT & ANALYTICS", "Financial & Inventory Breakdown", "report-sector", () -> btnReportManagementOnAction(null)));
        }

        int n = activeModules.size();

        Font font = Font.font("Segoe UI", FontWeight.BOLD, 12.0);
        if (font == null || font.getFamily().equals("System")) {
            font = Font.font("System", FontWeight.BOLD, 12.0);
        }

        if (n == 1) {
            // Single section selected (e.g. Cashier user granted 1 operational permission)
            // Render seamless full 360° annular ring donut with zero downward movement
            NavModule mod = activeModules.get(0);
            String normalPath = "M 0,-230.00 A 230.00,230.00 0 1,1 0,230.00 A 230.00,230.00 0 1,1 0,-230.00 " +
                                "M 0,-150.00 A 150.00,150.00 0 1,0 0,150.00 A 150.00,150.00 0 1,0 0,-150.00 Z";
            String hoverPath  = "M 0,-238.00 A 238.00,238.00 0 1,1 0,238.00 A 238.00,238.00 0 1,1 0,-238.00 " +
                                "M 0,-150.00 A 150.00,150.00 0 1,0 0,150.00 A 150.00,150.00 0 1,0 0,-150.00 Z";
            SVGPath sector = new SVGPath();
            sector.setContent(normalPath);
            sector.setLayoutX(300.0);
            sector.setLayoutY(270.0);
            sector.getStyleClass().add(mod.styleClass);
            sector.setCursor(Cursor.HAND);
            sector.setPickOnBounds(false);

            Group textGroup = buildCurvedTextGroup(mod.label, 300.0, 270.0, 190.0, 270.0, font);

            bindNavSectionEvents(sector, textGroup, normalPath, hoverPath, mod);

            svgWheelGroup.getChildren().add(sector);
            curvedLabelsGroup.getChildren().add(textGroup);
            return;
        }

        double span = 360.0 / n;
        // Align top-most sector centered on 270° (12 o'clock)
        double start0 = 270.0 - (span / 2.0);

        // Sleek refined channel spacing between nav sectors (~5-7px margin)
        double gapDelta = 1.0;
        double effectiveSpan = span - (2.0 * gapDelta);

        for (int i = 0; i < n; i++) {
            NavModule mod = activeModules.get(i);
            double baseStart = (start0 + i * span) % 360.0;
            if (baseStart < 0) baseStart += 360.0;
            double effectiveStart = (baseStart + gapDelta) % 360.0;
            double midAngle = baseStart + (span / 2.0);
            double normalizedMid = (midAngle % 360.0 + 360.0) % 360.0;

            String normalPath = createAnnularSectorPath(0, 0, 150.0, 230.0, effectiveStart, effectiveSpan);
            // Universal outward expansion (rInner 150.0 -> rOuter 238.0) matching Order Management for all sectors
            String hoverPath = createAnnularSectorPath(0, 0, 150.0, 238.0, effectiveStart, effectiveSpan);

            SVGPath sector = new SVGPath();
            sector.setContent(normalPath);
            sector.setLayoutX(300.0);
            sector.setLayoutY(270.0);
            sector.getStyleClass().add(mod.styleClass);
            sector.setCursor(Cursor.HAND);
            sector.setPickOnBounds(false);

            Group textGroup = buildCurvedTextGroup(mod.label, 300.0, 270.0, 190.0, midAngle, font);

            bindNavSectionEvents(sector, textGroup, normalPath, hoverPath, mod);

            svgWheelGroup.getChildren().add(sector);
            curvedLabelsGroup.getChildren().add(textGroup);
        }
    }

    private void bindNavSectionEvents(SVGPath sector, Group textGroup, String normalPath, String hoverPath, NavModule mod) {
        AtomicBoolean isPressed = new AtomicBoolean(false);
        AtomicLong lastClickTime = new AtomicLong(0);

        DropShadow hoverEffect = new DropShadow();
        hoverEffect.setColor(Color.rgb(245, 158, 11, 0.45));
        hoverEffect.setRadius(10.0);
        hoverEffect.setOffsetY(0.0);
        hoverEffect.setSpread(0.15);

        Runnable doClick = () -> {
            long now = System.currentTimeMillis();
            if (now - lastClickTime.get() < 500) {
                return;
            }
            lastClickTime.set(now);

            sector.setContent(normalPath);
            sector.setEffect(null);
            sector.setOpacity(1.0);
            dispatchModuleAction(mod.clickAction);
        };

        // Outward expansion matching Order Management with golden glow hover effect
        EventHandler<MouseEvent> enterHandler = e -> {
            sector.setContent(hoverPath);
            sector.setEffect(hoverEffect);
            activeHoveredModule = mod;
            currentOrbAction = mod.clickAction;
            showOrbHoverInfo(mod.key, mod.title, mod.subtitle);
        };

        // When mouse exits: restore normal path with content rock-solid still
        EventHandler<MouseEvent> exitHandler = e -> {
            sector.setContent(normalPath);
            sector.setEffect(null);
            sector.setOpacity(1.0);
        };

        // Button tactile feedback: slight opacity change on press with ZERO coordinate shift
        EventHandler<MouseEvent> pressHandler = e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                isPressed.set(true);
                sector.setOpacity(0.90);
            }
        };

        // Button release: robust click dispatch
        EventHandler<MouseEvent> releaseHandler = e -> {
            sector.setOpacity(1.0);
            if (isPressed.getAndSet(false)) {
                if (e.getButton() == MouseButton.PRIMARY || e.getButton() == null) {
                    doClick.run();
                }
            }
        };

        // Fallback synthetic mouse clicked
        EventHandler<MouseEvent> clickHandler = e -> {
            if (e.getButton() == MouseButton.PRIMARY || e.getButton() == null) {
                doClick.run();
            }
        };

        sector.setOnMouseEntered(enterHandler);
        sector.setOnMouseExited(exitHandler);
        sector.setOnMousePressed(pressHandler);
        sector.setOnMouseReleased(releaseHandler);
        sector.setOnMouseClicked(clickHandler);

        if (textGroup != null) {
            textGroup.setMouseTransparent(true);
            textGroup.setPickOnBounds(false);
        }
    }

    private boolean hasAccess(User user, String moduleKey) {
        if (user == null) {
            // Default cashier permissions
            return "ORDER".equals(moduleKey) || "PRODUCT".equals(moduleKey) || "REPORT".equals(moduleKey);
        }
        if (user.isAdmin()) return true;
        return user.hasPermission(moduleKey);
    }

    private String createAnnularSectorPath(double cx, double cy, double rInner, double rOuter, double startAngleDeg, double sweepAngleDeg) {
        double endAngleDeg = startAngleDeg + sweepAngleDeg;
        double a1Rad = Math.toRadians(startAngleDeg);
        double a2Rad = Math.toRadians(endAngleDeg);

        double x1Out = cx + rOuter * Math.cos(a1Rad);
        double y1Out = cy + rOuter * Math.sin(a1Rad);
        double x2Out = cx + rOuter * Math.cos(a2Rad);
        double y2Out = cy + rOuter * Math.sin(a2Rad);

        double x2In = cx + rInner * Math.cos(a2Rad);
        double y2In = cy + rInner * Math.sin(a2Rad);
        double x1In = cx + rInner * Math.cos(a1Rad);
        double y1In = cy + rInner * Math.sin(a1Rad);

        int largeArc = (sweepAngleDeg > 180.0) ? 1 : 0;

        return String.format(Locale.US,
                "M %.2f,%.2f A %.2f,%.2f 0 %d,1 %.2f,%.2f L %.2f,%.2f A %.2f,%.2f 0 %d,0 %.2f,%.2f Z",
                x1Out, y1Out, rOuter, rOuter, largeArc, x2Out, y2Out,
                x2In, y2In, rInner, rInner, largeArc, x1In, y1In);
    }

    private Group buildCurvedTextGroup(String text, double cx, double cy, double radius, double midAngleDeg, Font font) {
        Group group = new Group();
        group.setMouseTransparent(true);

        int n = text.length();
        if (n == 0) return group;

        double normalizedMid = (midAngleDeg % 360.0 + 360.0) % 360.0;
        double[] widths = new double[n];
        double charSpacing = 1.0;
        double totalArcLength = 0;

        for (int i = 0; i < n; i++) {
            char ch = text.charAt(i);
            if (ch == ' ') {
                widths[i] = 5.0;
            } else {
                Text dummy = new Text(String.valueOf(ch));
                dummy.setFont(font);
                widths[i] = dummy.getLayoutBounds().getWidth();
            }
            totalArcLength += widths[i];
            if (i > 0) totalArcLength += charSpacing;
        }

        double totalSpanRad = totalArcLength / radius;
        double midAngleRad = Math.toRadians(normalizedMid);

        boolean isLower = (normalizedMid > 0 && normalizedMid < 180);

        DropShadow textShadow = new DropShadow();
        textShadow.setColor(Color.rgb(0, 0, 0, 0.65));
        textShadow.setRadius(4.0);
        textShadow.setOffsetY(1.5);

        if (!isLower) {
            // Upper half: reads clockwise left-to-right
            double currentAngleRad = midAngleRad - (totalSpanRad / 2.0);

            for (int i = 0; i < n; i++) {
                char ch = text.charAt(i);
                double charSpanRad = widths[i] / radius;
                double charCenterRad = currentAngleRad + (charSpanRad / 2.0);

                if (ch != ' ') {
                    double x = cx + radius * Math.cos(charCenterRad);
                    double y = cy + radius * Math.sin(charCenterRad);
                    double rotationDeg = Math.toDegrees(charCenterRad) + 90.0;

                    Text t = new Text(String.valueOf(ch));
                    t.setFont(font);
                    t.setFill(Color.WHITE);
                    t.setTextOrigin(VPos.CENTER);
                    t.setTextAlignment(TextAlignment.CENTER);
                    t.setEffect(textShadow);

                    t.setLayoutX(x - widths[i] / 2.0);
                    t.setLayoutY(y);
                    t.setRotate(rotationDeg);

                    group.getChildren().add(t);
                }

                currentAngleRad += charSpanRad + (charSpacing / radius);
            }
        } else {
            // Lower half: reads upright left-to-right
            double currentAngleRad = midAngleRad + (totalSpanRad / 2.0);

            for (int i = 0; i < n; i++) {
                char ch = text.charAt(i);
                double charSpanRad = widths[i] / radius;
                double charCenterRad = currentAngleRad - (charSpanRad / 2.0);

                if (ch != ' ') {
                    double x = cx + radius * Math.cos(charCenterRad);
                    double y = cy + radius * Math.sin(charCenterRad);
                    double rotationDeg = Math.toDegrees(charCenterRad) - 90.0;

                    Text t = new Text(String.valueOf(ch));
                    t.setFont(font);
                    t.setFill(Color.WHITE);
                    t.setTextOrigin(VPos.CENTER);
                    t.setTextAlignment(TextAlignment.CENTER);
                    t.setEffect(textShadow);

                    t.setLayoutX(x - widths[i] / 2.0);
                    t.setLayoutY(y);
                    t.setRotate(rotationDeg);

                    group.getChildren().add(t);
                }

                currentAngleRad -= (charSpanRad + (charSpacing / radius));
            }
        }

        return group;
    }

    private void showOrbHoverInfo(String moduleKey, String title, String subtitle) {
        if (lblOrbIcon != null) {
            lblOrbIcon.setText("");
            lblOrbIcon.setGraphic(createOrbVectorBadge(moduleKey));
        }
        if (lblOrbTitle != null) lblOrbTitle.setText(title);
        if (lblOrbSubtitle != null) lblOrbSubtitle.setText(subtitle);

        if (orbIdleBox != null && orbHoverBox != null) {
            if (currentOrbFade != null) {
                currentOrbFade.stop();
            }

            orbIdleBox.setVisible(false);
            orbIdleBox.setOpacity(0.0);

            orbHoverBox.setVisible(true);
            orbHoverBox.setOpacity(1.0);

            currentOrbFade = new FadeTransition(Duration.millis(140), orbHoverBox);
            currentOrbFade.setFromValue(0.4);
            currentOrbFade.setToValue(1.0);
            currentOrbFade.play();
        }
    }

    private StackPane createOrbVectorBadge(String moduleKey) {
        StackPane badge = new StackPane();
        badge.setPrefSize(56, 56);
        badge.setMinSize(56, 56);
        badge.setMaxSize(56, 56);

        Circle circle = new Circle(28);
        circle.setStrokeWidth(1.8);

        SVGPath iconPath = new SVGPath();
        iconPath.setStrokeWidth(2.0);
        iconPath.setStrokeLineCap(StrokeLineCap.ROUND);
        iconPath.setStrokeLineJoin(StrokeLineJoin.ROUND);
        iconPath.setFill(Color.TRANSPARENT);

        String strokeColor = "#d97706";
        String fillColor = "#fffbeb";
        String borderColor = "#fde68a";
        String pathData = "";

        if (moduleKey != null) {
            switch (moduleKey) {
                case "ORDER":
                    // Luxury Boutique Shopping Bag with handle and fold crease
                    fillColor = "#fdf2f8";
                    borderColor = "#fbcfe8";
                    strokeColor = "#db2777";
                    pathData = "M 6 2 L 3 6 L 3 20 C 3 21.1 3.9 22 5 22 L 19 22 C 20.1 22 21 21.1 21 20 L 21 6 L 18 2 Z M 3 6 L 21 6 M 16 10 C 16 12.2 14.2 14 12 14 C 9.8 14 8 12.2 8 10";
                    break;
                case "PRODUCT":
                    // Haute Couture Tailored Shirt with collar and cuffs
                    fillColor = "#fffbeb";
                    borderColor = "#fde68a";
                    strokeColor = "#d97706";
                    pathData = "M 20.38 3.46 L 16 2 C 14.5 3.5 9.5 3.5 8 2 L 3.62 3.46 C 2.65 3.7 2 4.6 2 5.6 L 2 10 C 2 11.1 2.9 12 4 12 L 6 12 L 6 21 C 6 21.6 6.4 22 7 22 L 17 22 C 17.6 22 18 21.6 18 21 L 18 12 L 20 12 C 21.1 12 22 11.1 22 10 L 22 5.6 C 22 4.6 21.35 3.7 20.38 3.46 Z M 16 2 C 14.5 4 9.5 4 8 2 M 12 5 L 12 22";
                    break;
                case "REPORT":
                    // Analytics growth chart with upward trendline & arrow
                    fillColor = "#ecfdf5";
                    borderColor = "#a7f3d0";
                    strokeColor = "#059669";
                    pathData = "M 3 3 L 3 20 C 3 20.6 3.4 21 4 21 L 21 21 M 7 17 L 7 13 M 12 17 L 12 9 M 17 17 L 17 5 M 6 11 L 11 7 L 15 10 L 20 4 M 16 4 L 20 4 L 20 8";
                    break;
                case "EMPLOYEE":
                    // Boutique Staff & Atelier Team (2 members in perspective)
                    fillColor = "#f5f3ff";
                    borderColor = "#ddd6fe";
                    strokeColor = "#7c3aed";
                    pathData = "M 16 21 L 16 19 C 16 16.8 14.2 15 12 15 L 6 15 C 3.8 15 2 16.8 2 19 L 2 21 M 9 11 C 11.2 11 13 9.2 13 7 C 13 4.8 11.2 3 9 3 C 6.8 3 5 4.8 5 7 C 5 9.2 6.8 11 9 11 Z M 16 3.1 C 17.8 3.6 19 5.1 19 7 C 19 8.9 17.8 10.4 16 10.9 M 22 21 L 22 19 C 22 17.2 20.8 15.7 19.1 15.2";
                    break;
                case "SUPPLIER":
                    // Wholesale delivery truck / textile supplier
                    fillColor = "#f0f9ff";
                    borderColor = "#bae6fd";
                    strokeColor = "#0284c7";
                    pathData = "M 1 4 L 14 4 L 14 16 L 1 16 Z M 14 8 L 19 8 L 22 11 L 22 16 L 14 16 M 1 16 L 3.5 16 M 7.5 16 L 15.5 16 M 19.5 16 L 22 16 M 5.5 18 A 2 2 0 1 0 5.5 14 A 2 2 0 0 0 5.5 18 Z M 17.5 18 A 2 2 0 1 0 17.5 14 A 2 2 0 0 0 17.5 18 Z";
                    break;
                case "USER":
                    // Authenticated user security shield
                    fillColor = "#eff6ff";
                    borderColor = "#bfdbfe";
                    strokeColor = "#2563eb";
                    pathData = "M 12 22 C 12 22 20 18 20 12 L 20 5 L 12 2 L 4 5 L 4 12 C 4 18 12 22 12 22 Z M 12 11 C 13.1 11 14 10.1 14 9 C 14 7.9 13.1 7 12 7 C 10.9 7 10 7.9 10 9 C 10 10.1 10.9 11 12 11 Z M 8.5 16.5 C 8.5 15 10 14 12 14 C 14 14 15.5 15 15.5 16.5";
                    break;
            }
        }

        circle.setFill(Color.web(fillColor));
        circle.setStroke(Color.web(borderColor));
        iconPath.setContent(pathData);
        iconPath.setStroke(Color.web(strokeColor));
        iconPath.setScaleX(1.35);
        iconPath.setScaleY(1.35);

        badge.getChildren().addAll(circle, iconPath);
        return badge;
    }

    private void hideOrbHoverInfo() {
        if (orbHoverBox != null && orbIdleBox != null) {
            if (currentOrbFade != null) {
                currentOrbFade.stop();
            }

            orbHoverBox.setVisible(false);
            orbHoverBox.setOpacity(0.0);

            orbIdleBox.setVisible(true);
            orbIdleBox.setOpacity(1.0);

            currentOrbFade = new FadeTransition(Duration.millis(140), orbIdleBox);
            currentOrbFade.setFromValue(0.4);
            currentOrbFade.setToValue(1.0);
            currentOrbFade.play();
        }
    }

    // =========================================================================
    // MODULE NAVIGATION ACTIONS
    // =========================================================================

    @FXML
    public void btnProductManagementOnAction(ActionEvent event) {
        openModuleStage("/view/item_form.fxml", "Clothify - Product & Apparel Stock Management", 960, 640);
    }

    @FXML
    public void btnOrderManagementOnAction(ActionEvent event) {
        openModuleStage("/view/pos_order_view.fxml", "Clothify - Point of Sale (POS) Register", 1120, 720);
    }

    @FXML
    public void btnOrderHistoryOnAction(Event event) {
        openModuleStage("/view/order_history_view.fxml", "Clothify - Order Records & Transaction History", 1140, 720);
    }

    @FXML
    public void btnSupplierManagementOnAction(ActionEvent event) {
        User user = UserSession.getInstance().getCurrentUser();
        if (!hasAccess(user, "SUPPLIER")) {
            AlertUtil.showWarning("Access Denied", "You do not have permission to access Supplier Management.");
            return;
        }
        openModuleStage("/view/supplier_view.fxml", "Clothify - Supplier Network & Purchase Tracking", 1180, 720);
    }

    @FXML
    public void btnEmployeeManagementOnAction(ActionEvent event) {
        User user = UserSession.getInstance().getCurrentUser();
        if (!hasAccess(user, "EMPLOYEE")) {
            AlertUtil.showWarning("Access Denied", "You do not have permission to access Employee Management.");
            return;
        }
        openModuleStage("/view/employee_view.fxml", "Clothify - Employee Roster & Staff Directory", 1260, 720);
    }

    @FXML
    public void btnUserManagementOnAction(ActionEvent event) {
        User user = UserSession.getInstance().getCurrentUser();
        if (!hasAccess(user, "USER") && !UserSession.getInstance().isAdmin()) {
            AlertUtil.showWarning("Access Denied", "Administrator privilege is required to access User Security Management.");
            return;
        }
        openModuleStage("/view/user_management_view.fxml", "Clothify - User Accounts & Role Permissions", 1160, 720);
    }

    @FXML
    public void btnReportManagementOnAction(ActionEvent event) {
        openModuleStage("/view/report_view.fxml", "Clothify - Financial Analytics & Inventory Reports", 1080, 700);
    }

    private void openModuleStage(String fxmlPath, String windowTitle, double width, double height) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(windowTitle);

            WindowUtil.centerInVisualBounds(stage, width, height);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh metrics and dynamic wheel after module closes
            loadDatabaseMetrics();
            initDynamicWheel();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Module Error", "Could not load module [" + fxmlPath + "]: " + e.getMessage());
        }
    }

    @FXML
    public void btnSignOutOnAction(ActionEvent event) {
        boolean confirm = AlertUtil.showConfirmation(
                "Sign Out",
                "Sign Out of Clothify Atelier",
                "Are you sure you want to end your current session and return to the login screen?"
        );

        if (confirm) {
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            UserSession.getInstance().clearSession();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                Stage stage = new Stage();
                Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user_login_form.fxml"))));
                stage.setScene(scene);
                stage.setTitle("Clothify Store - Fashion POS & ERP Management System");
                stage.setResizable(false);
                WindowUtil.centerInVisualBounds(stage, 940, 580);
                stage.show();
                currentStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showError("Navigation Error", "Could not return to login form: " + e.getMessage());
            }
        }
    }
}
