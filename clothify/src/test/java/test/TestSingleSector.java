package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import model.User;
import security.UserSession;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestSingleSector {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch testLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // User with ONLY 1 permission: ORDER
                User u = new User("USR-1", "cashier1@clothify.com", "pass", "Mr", "SinglePermUser",
                        LocalDate.of(1995, 5, 5), 45000.0, "Galle Rd", "Colombo", "Western", "CASHIER", "ACTIVE", "ORDER");
                UserSession.getInstance().login(u);

                FXMLLoader loader = new FXMLLoader(TestSingleSector.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1240, 780);
                root.applyCss();
                root.layout();

                Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                fWheel.setAccessible(true);
                javafx.scene.Group wheelGroup = (javafx.scene.Group) fWheel.get(controller);

                Field fLabels = DashboardFormController.class.getDeclaredField("curvedLabelsGroup");
                fLabels.setAccessible(true);
                javafx.scene.Group labelsGroup = (javafx.scene.Group) fLabels.get(controller);

                System.out.println("Wheel group children count: " + wheelGroup.getChildren().size());
                System.out.println("Labels group children count: " + labelsGroup.getChildren().size());

                if (wheelGroup.getChildren().size() > 0) {
                    SVGPath sector = (SVGPath) wheelGroup.getChildren().get(0);
                    System.out.println("Sector content: " + sector.getContent());
                    System.out.println("Sector boundsInLocal: " + sector.getBoundsInLocal());
                    System.out.println("Sector boundsInParent: " + sector.getBoundsInParent());
                    System.out.println("Sector pickOnBounds: " + sector.isPickOnBounds());

                    // Test hit-testing at various points
                    // Layout is (300, 270). Outer radius 230, inner 150.
                    // Point at top (12 o'clock): local x = 0, y = -190.
                    boolean containsTop = sector.contains(0, -190);
                    System.out.println("sector.contains(0, -190) [Top, 12 o'clock]: " + containsTop);

                    // Point at right (3 o'clock): local x = 190, y = 0.
                    boolean containsRight = sector.contains(190, 0);
                    System.out.println("sector.contains(190, 0) [Right, 3 o'clock]: " + containsRight);

                    // Point at left (9 o'clock): local x = -190, y = 0.
                    boolean containsLeft = sector.contains(-190, 0);
                    System.out.println("sector.contains(-190, 0) [Left, 9 o'clock]: " + containsLeft);

                    // Point at bottom gap (6 o'clock): local x = 0, y = 190.
                    boolean containsBottom = sector.contains(0, 190);
                    System.out.println("sector.contains(0, 190) [Bottom, 6 o'clock]: " + containsBottom);

                    // Test mouse click firing
                    AtomicBoolean clicked = new AtomicBoolean(false);
                    // Override click action or trigger event
                    MouseEvent clickEvent = new MouseEvent(
                            MouseEvent.MOUSE_CLICKED,
                            0, -190, 300, 80,
                            MouseButton.PRIMARY, 1,
                            false, false, false, false,
                            true, false, false, false, false, false, null
                    );
                    sector.fireEvent(clickEvent);
                }

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                testLatch.countDown();
            }
        });

        testLatch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }
}
