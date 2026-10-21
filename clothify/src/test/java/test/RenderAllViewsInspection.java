package test;

import controller.dashboard.DashboardFormController;
import controller.user.UserLoginFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import model.User;
import security.UserSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RenderAllViewsInspection {

    private static final String OUT_DIR = "C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/inspect_";

    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        User admin = new User("1", "one@gmail.com", "one", "Mr", "Danapala",
                LocalDate.of(1990, 1, 1), 85000.0, "Headquarters", "Colombo", "Western",
                "ADMIN", "ACTIVE", "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");

        User cashier = new User("2", "two@gmail.com", "two", "Mr", "Gunapala",
                LocalDate.of(1995, 5, 5), 45000.0, "Boutique", "Colombo", "Western",
                "CASHIER", "ACTIVE", "ORDER,PRODUCT,REPORT");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // 1. Login (Admin selected)
                renderView("/view/user_login_form.fxml", 980, 620, "01_login_admin.png", null);

                // 2. Login (Cashier selected)
                renderView("/view/user_login_form.fxml", 980, 620, "02_login_cashier.png", ctrl -> {
                    try {
                        Method m = UserLoginFormController.class.getDeclaredMethod("onRoleCashierSelected", javafx.event.ActionEvent.class);
                        m.setAccessible(true);
                        m.invoke(ctrl, (Object) null);
                    } catch (Exception ignored) {}
                });

                // 3. Login (Forgot Password pane)
                renderView("/view/user_login_form.fxml", 980, 620, "03_login_forgot_pass.png", ctrl -> {
                    try {
                        Method m = UserLoginFormController.class.getDeclaredMethod("onSwitchToForgotPassword", javafx.event.ActionEvent.class);
                        m.setAccessible(true);
                        m.invoke(ctrl, (Object) null);
                    } catch (Exception ignored) {}
                });

                // 4. Login (Register Staff pane)
                renderView("/view/user_login_form.fxml", 980, 620, "04_login_register.png", ctrl -> {
                    try {
                        Method m = UserLoginFormController.class.getDeclaredMethod("onSwitchToRegister", javafx.event.ActionEvent.class);
                        m.setAccessible(true);
                        m.invoke(ctrl, (Object) null);
                    } catch (Exception ignored) {}
                });

                // 5. Admin Dashboard
                UserSession.getInstance().login(admin);
                renderView("/view/dashboard_form.fxml", 1260, 720, "05_admin_dashboard.png", null);

                // 6. Cashier Dashboard
                UserSession.getInstance().login(cashier);
                renderView("/view/dashboard_form.fxml", 1260, 720, "06_cashier_dashboard.png", null);

                // Switch back to admin for remaining views
                UserSession.getInstance().login(admin);

                // 7. POS Order View
                renderView("/view/pos_order_view.fxml", 1120, 700, "07_pos_order.png", null);

                // 8. Item Form (Normal 1050x700)
                renderView("/view/item_form.fxml", 1050, 700, "08_item_form_normal.png", null);

                // 9. Item Form (Maximized / Wide 1366x768)
                renderView("/view/item_form.fxml", 1366, 768, "09_item_form_wide.png", null);

                // 10. Item Management (Catalog Grid)
                renderView("/view/item_management_view.fxml", 1050, 700, "10_item_management.png", null);

                // 11. Order History
                renderView("/view/order_history_view.fxml", 1140, 720, "11_order_history.png", null);

                // 12. Employee View
                renderView("/view/employee_view.fxml", 1260, 720, "12_employee_view.png", null);

                // 13. Supplier View
                renderView("/view/supplier_view.fxml", 1180, 720, "13_supplier_view.png", null);

                // 14. Report View
                renderView("/view/report_view.fxml", 1100, 720, "14_report_view.png", null);

                // 15. User Management View
                renderView("/view/user_management_view.fxml", 1160, 720, "15_user_management.png", null);

                // 16. Overview View
                renderView("/view/overview_view.fxml", 1140, 720, "16_overview_view.png", null);

                System.out.println("ALL_16_VIEWS_RENDERED_FOR_INSPECTION");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
    }

    interface ControllerConfigurator {
        void configure(Object controller);
    }

    private static void renderView(String fxmlPath, double w, double h, String filename, ControllerConfigurator configurator) throws Exception {
        FXMLLoader loader = new FXMLLoader(RenderAllViewsInspection.class.getResource(fxmlPath));
        Parent root = loader.load();
        if (configurator != null) {
            configurator.configure(loader.getController());
        }
        Scene scene = new Scene(root, w, h);
        root.applyCss();
        root.layout();

        WritableImage snap = root.snapshot(null, null);
        BufferedImage bImg = new BufferedImage((int) snap.getWidth(), (int) snap.getHeight(), BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = snap.getPixelReader();
        for (int y = 0; y < (int) snap.getHeight(); y++) {
            for (int x = 0; x < (int) snap.getWidth(); x++) {
                bImg.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        File outFile = new File(OUT_DIR + filename);
        ImageIO.write(bImg, "png", outFile);
        System.out.println("RENDERED: " + filename);
    }
}
