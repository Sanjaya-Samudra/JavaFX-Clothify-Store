package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import model.User;
import security.UserSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RenderCashierRadialSnapshots {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        // Cashier user with standard 3 permissions: ORDER, PRODUCT, REPORT
        User cashier = new User("USR-CASH-01", "cashier@clothify.com", "pass", "Ms", "Sarah Jenkins",
                LocalDate.of(1997, 8, 14), 45000.0, "High St", "Colombo", "Western", "CASHIER", "ACTIVE", "ORDER,PRODUCT,REPORT");
        UserSession.getInstance().login(cashier);

        // 1. Idle Cashier Dashboard
        renderSnapshot(-1, "dashboard_cashier_idle_verified.png");

        // 2. Hover Order Management (Sector 0)
        renderSnapshot(0, "dashboard_cashier_hover_order.png");

        // 3. Hover Product Management (Sector 1)
        renderSnapshot(1, "dashboard_cashier_hover_product.png");

        // 4. Hover Report Management (Sector 2)
        renderSnapshot(2, "dashboard_cashier_hover_report.png");

        System.out.println("ALL_CASHIER_SNAPSHOTS_RENDERED_SUCCESSFULLY");
        System.exit(0);
    }

    private static void renderSnapshot(int hoverIndex, String outName) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(RenderCashierRadialSnapshots.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1240, 780);
                root.applyCss();
                root.layout();

                if (hoverIndex >= 0) {
                    Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                    fWheel.setAccessible(true);
                    Group wheelGroup = (Group) fWheel.get(controller);

                    if (hoverIndex < wheelGroup.getChildren().size()) {
                        SVGPath sector = (SVGPath) wheelGroup.getChildren().get(hoverIndex);
                        MouseEvent enter = new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0,
                                false, false, false, false, false, false, false, false, false, false, null);
                        sector.fireEvent(enter);
                    }
                }

                root.applyCss();
                root.layout();

                WritableImage snapshot = root.snapshot(null, null);
                BufferedImage bImg = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(), BufferedImage.TYPE_INT_ARGB);
                PixelReader reader = snapshot.getPixelReader();
                for (int y = 0; y < (int) snapshot.getHeight(); y++) {
                    for (int x = 0; x < (int) snapshot.getWidth(); x++) {
                        bImg.setRGB(x, y, reader.getArgb(x, y));
                    }
                }

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/" + outName);
                ImageIO.write(bImg, "png", outFile);
                System.out.println("SAVED: " + outFile.getAbsolutePath());

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
    }
}
