package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import model.User;
import security.UserSession;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestAdminHoverBounds {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try { Platform.startup(startupLatch::countDown); } catch (Exception e) { startupLatch.countDown(); }
        startupLatch.await(5, TimeUnit.SECONDS);

        User admin = new User("1", "admin@clothify.com", "pass", "Mr", "Admin",
                LocalDate.of(1990, 1, 1), 75000.0, "HQ", "Colombo", "Western", "ADMIN", "ACTIVE",
                "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
        UserSession.getInstance().login(admin);

        CountDownLatch testDone = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(TestAdminHoverBounds.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1200, 700);
                root.applyCss();
                root.layout();

                Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                fWheel.setAccessible(true);
                Group wheelGroup = (Group) fWheel.get(controller);

                double idleMaxY = -1;
                for (int i = 0; i < wheelGroup.getChildren().size(); i++) {
                    SVGPath s = (SVGPath) wheelGroup.getChildren().get(i);
                    Bounds b = s.localToScene(s.getBoundsInLocal());
                    System.out.println("Idle Sector " + i + " (" + s.getStyleClass() + "): y=" + String.format("%.2f..%.2f", b.getMinY(), b.getMaxY()));
                    if (b.getMaxY() > idleMaxY) idleMaxY = b.getMaxY();
                }
                System.out.println("IDLE OVERALL MAX Y: " + String.format("%.2f", idleMaxY));

                for (int i = 0; i < wheelGroup.getChildren().size(); i++) {
                    SVGPath s = (SVGPath) wheelGroup.getChildren().get(i);
                    // trigger enter
                    s.getOnMouseEntered().handle(new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0, false, false, false, false, false, false, false, false, false, false, null));
                    root.applyCss();
                    root.layout();

                    double hoverOverallMaxY = -1;
                    for (int j = 0; j < wheelGroup.getChildren().size(); j++) {
                        SVGPath s2 = (SVGPath) wheelGroup.getChildren().get(j);
                        Bounds b = s2.localToScene(s2.getBoundsInLocal());
                        if (b.getMaxY() > hoverOverallMaxY) hoverOverallMaxY = b.getMaxY();
                    }

                    Bounds b = s.localToScene(s.getBoundsInLocal());
                    double delta = hoverOverallMaxY - idleMaxY;
                    System.out.println("Hover Sector " + i + " (" + s.getStyleClass() + "): sectorMaxY=" + String.format("%.2f", b.getMaxY()) + ", overallMaxY=" + String.format("%.2f", hoverOverallMaxY) + ", delta=" + String.format("%.2f", delta));

                    // trigger exit
                    s.getOnMouseExited().handle(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.NONE, 0, false, false, false, false, false, false, false, false, false, false, null));
                    root.applyCss();
                    root.layout();
                }

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                testDone.countDown();
            }
        });

        testDone.await(5, TimeUnit.SECONDS);
        System.exit(0);
    }
}
