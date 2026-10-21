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

public class RenderCashierDashboardSnapshots {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        // Standard Cashier user with default 3 permissions: ORDER, PRODUCT, REPORT
        User cashier = new User("USR-CASH-01", "cashier@clothify.com", "pass", "Ms", "Sarah Jenkins",
                LocalDate.of(1997, 8, 14), 45000.0, "High St", "Colombo", "Western", "CASHIER", "ACTIVE",
                "ORDER,PRODUCT,REPORT");
        UserSession.getInstance().login(cashier);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(RenderCashierDashboardSnapshots.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1280, 780);
                root.applyCss();
                root.layout();

                Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                fWheel.setAccessible(true);
                Group wheelGroup = (Group) fWheel.get(controller);

                saveSnapshot(root, "cashier_dashboard_idle.png");

                // In Cashier mode:
                // Module 0: ORDER
                // Module 1: PRODUCT
                // Module 2: REPORT
                String[] names = {"order", "product", "report"};
                for (int i = 0; i < wheelGroup.getChildren().size() && i < names.length; i++) {
                    fireHover(wheelGroup, i);
                    root.applyCss();
                    root.layout();
                    saveSnapshot(root, "cashier_dashboard_hover_" + names[i] + ".png");
                    fireExit(wheelGroup, i);
                    root.applyCss();
                    root.layout();
                }

                System.out.println("ALL_CASHIER_SNAPSHOTS_SAVED_SUCCESSFULLY");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
    }

    private static void fireHover(Group wheelGroup, int index) {
        SVGPath sector = (SVGPath) wheelGroup.getChildren().get(index);
        MouseEvent enter = new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0,
                false, false, false, false, false, false, false, false, false, false, null);
        sector.fireEvent(enter);
    }

    private static void fireExit(Group wheelGroup, int index) {
        SVGPath sector = (SVGPath) wheelGroup.getChildren().get(index);
        MouseEvent exit = new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.NONE, 0,
                false, false, false, false, false, false, false, false, false, false, null);
        sector.fireEvent(exit);
    }

    private static void saveSnapshot(Parent root, String filename) throws Exception {
        WritableImage snap = root.snapshot(null, null);
        BufferedImage bImg = new BufferedImage((int) snap.getWidth(), (int) snap.getHeight(), BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = snap.getPixelReader();
        for (int y = 0; y < (int) snap.getHeight(); y++) {
            for (int x = 0; x < (int) snap.getWidth(); x++) {
                bImg.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/" + filename);
        ImageIO.write(bImg, "png", outFile);
        System.out.println("SAVED: " + outFile.getAbsolutePath());
    }
}
