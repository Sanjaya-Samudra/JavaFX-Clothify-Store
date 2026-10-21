package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
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

public class InspectHoverBoundsShift {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        User cashier = new User("USR-CASH-01", "cashier@clothify.com", "pass", "Ms", "Sarah Jenkins",
                LocalDate.of(1997, 8, 14), 45000.0, "High St", "Colombo", "Western", "CASHIER", "ACTIVE", "ORDER,PRODUCT,REPORT");
        UserSession.getInstance().login(cashier);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(InspectHoverBoundsShift.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1240, 780);
                root.applyCss();
                root.layout();

                Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                fWheel.setAccessible(true);
                Group wheelGroup = (Group) fWheel.get(controller);

                dumpInfo("IDLE", root, wheelGroup);

                fireHover(wheelGroup, 1);
                root.applyCss();
                root.layout();
                dumpInfo("HOVER PRODUCT", root, wheelGroup);

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }

    private static void fireHover(Group wheelGroup, int index) {
        SVGPath sector = (SVGPath) wheelGroup.getChildren().get(index);
        MouseEvent enter = new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0,
                false, false, false, false, false, false, false, false, false, false, null);
        sector.fireEvent(enter);
    }

    private static void dumpInfo(String state, Parent root, Group wheelGroup) {
        System.out.println("=== " + state + " ===");
        // Find bottom bar
        javafx.scene.layout.VBox mainVBox = (javafx.scene.layout.VBox) ((javafx.scene.layout.AnchorPane) root).getChildren().get(1);
        Node topBar = mainVBox.getChildren().get(0);
        Node centerHBox = mainVBox.getChildren().get(1);
        Node bottomBar = mainVBox.getChildren().get(2);

        System.out.printf("  mainVBox height=%.2f\n", mainVBox.getHeight());
        System.out.printf("  topBar boundsInScene: y=%.2f..%.2f, h=%.2f\n",
                topBar.localToScene(topBar.getBoundsInLocal()).getMinY(),
                topBar.localToScene(topBar.getBoundsInLocal()).getMaxY(),
                topBar.getBoundsInLocal().getHeight());
        System.out.printf("  centerHBox boundsInScene: y=%.2f..%.2f, h=%.2f\n",
                centerHBox.localToScene(centerHBox.getBoundsInLocal()).getMinY(),
                centerHBox.localToScene(centerHBox.getBoundsInLocal()).getMaxY(),
                centerHBox.getBoundsInLocal().getHeight());
        System.out.printf("  bottomBar boundsInScene: y=%.2f..%.2f, h=%.2f\n",
                bottomBar.localToScene(bottomBar.getBoundsInLocal()).getMinY(),
                bottomBar.localToScene(bottomBar.getBoundsInLocal()).getMaxY(),
                bottomBar.getBoundsInLocal().getHeight());

        for (int i = 0; i < wheelGroup.getChildren().size(); i++) {
            Node s = wheelGroup.getChildren().get(i);
            Bounds b = s.localToScene(s.getBoundsInLocal());
            System.out.printf("  Sector %d boundsInScene: y=%.2f..%.2f, h=%.2f\n", i, b.getMinY(), b.getMaxY(), b.getHeight());
        }
    }
}
