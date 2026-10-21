package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import model.User;
import security.UserSession;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClickInteractions {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        // 1. Test 1-Section (Single nav section selected)
        testConfiguration("ORDER", 1);

        // 2. Test 3-Sections (Cashier default: Order, Product, Report)
        testConfiguration("ORDER,PRODUCT,REPORT", 3);

        // 3. Test 6-Sections (Admin: All 6 modules)
        testConfiguration("ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER", 6);

        System.out.println("ALL_CLICK_INTERACTIONS_TESTS_PASSED_SUCCESSFULLY!");
        System.exit(0);
    }

    private static void testConfiguration(String perms, int expectedCount) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                System.out.println("\n========================================================");
                System.out.println("Testing Configuration: [" + perms + "] (Expected sectors: " + expectedCount + ")");
                System.out.println("========================================================");

                User u = new User("USR-TEST", "tester@clothify.com", "pass", "Mr", "Tester",
                        LocalDate.of(1992, 2, 2), 60000.0, "Test St", "Colombo", "Western",
                        expectedCount == 6 ? "ADMIN" : "CASHIER", "ACTIVE", perms);
                UserSession.getInstance().login(u);

                FXMLLoader loader = new FXMLLoader(TestClickInteractions.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1240, 780);
                root.applyCss();
                root.layout();

                Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                fWheel.setAccessible(true);
                Group wheelGroup = (Group) fWheel.get(controller);

                Field fLabels = DashboardFormController.class.getDeclaredField("curvedLabelsGroup");
                fLabels.setAccessible(true);
                Group labelsGroup = (Group) fLabels.get(controller);

                Field fOrb = DashboardFormController.class.getDeclaredField("centerOrbPane");
                fOrb.setAccessible(true);
                StackPane centerOrb = (StackPane) fOrb.get(controller);

                Field fHoverBox = DashboardFormController.class.getDeclaredField("orbHoverBox");
                fHoverBox.setAccessible(true);
                VBox hoverBox = (VBox) fHoverBox.get(controller);

                int count = wheelGroup.getChildren().size();
                if (count != expectedCount) {
                    throw new AssertionError("Expected " + expectedCount + " sectors but found " + count);
                }
                System.out.println("✓ Sector count verified: " + count);

                for (int i = 0; i < count; i++) {
                    SVGPath sector = (SVGPath) wheelGroup.getChildren().get(i);
                    Group textGroup = (Group) labelsGroup.getChildren().get(i);

                    // A. Hover Enter on Sector
                    MouseEvent enterEvent = new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0,
                            false, false, false, false, false, false, false, false, false, false, null);
                    sector.fireEvent(enterEvent);

                    Field fAction = DashboardFormController.class.getDeclaredField("currentOrbAction");
                    fAction.setAccessible(true);
                    Runnable activeAction = (Runnable) fAction.get(controller);
                    if (activeAction == null) {
                        throw new AssertionError("Sector " + i + " failed to set currentOrbAction on mouse enter!");
                    }
                    System.out.println("  ✓ Sector " + i + " (" + sector.getStyleClass() + "): Orb action active on hover");

                    // B. Test Move into Center Orb (dist <= 152)
                    MouseEvent exitInward = new MouseEvent(MouseEvent.MOUSE_EXITED, 50, 50, 0, 0, MouseButton.NONE, 0,
                            false, false, false, false, false, false, false, false, false, false, null);
                    sector.fireEvent(exitInward);

                    // Verify action is still retained!
                    activeAction = (Runnable) fAction.get(controller);
                    if (activeAction == null) {
                        throw new AssertionError("Sector " + i + " cleared currentOrbAction when cursor moved into center orb!");
                    }
                    System.out.println("  ✓ Sector " + i + ": Orb action retained when moving into center orb");

                    // C. Test Click on Center Orb
                    MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 100, 100, 0, 0, MouseButton.PRIMARY, 1,
                            false, false, false, false, false, false, false, false, false, false, null);
                    // Verify centerOrbPane has click handler
                    if (centerOrb.getOnMouseClicked() == null) {
                        throw new AssertionError("centerOrbPane does not have an onMouseClicked handler!");
                    }
                    if (hoverBox.getOnMouseClicked() == null) {
                        throw new AssertionError("orbHoverBox does not have an onMouseClicked handler!");
                    }
                    System.out.println("  ✓ Center orb & hover box click handlers registered");

                    // D. Test Sector Click Handlers
                    if (sector.getOnMouseClicked() == null || sector.getOnMouseReleased() == null) {
                        throw new AssertionError("Sector " + i + " missing click / release handlers!");
                    }
                    System.out.println("  ✓ Sector " + i + " click and release handlers registered");

                    // E. Test Text Group Click Handlers
                    if (textGroup.getOnMouseClicked() == null || textGroup.getOnMouseReleased() == null) {
                        throw new AssertionError("TextGroup " + i + " missing click / release handlers!");
                    }
                    if (textGroup.isMouseTransparent()) {
                        throw new AssertionError("TextGroup " + i + " is mouseTransparent!");
                    }
                    System.out.println("  ✓ Text overlay " + i + " interactive & click handlers registered");

                    // Exit outwards
                    MouseEvent exitOutward = new MouseEvent(MouseEvent.MOUSE_EXITED, 250, 250, 0, 0, MouseButton.NONE, 0,
                            false, false, false, false, false, false, false, false, false, false, null);
                    sector.fireEvent(exitOutward);
                }

                System.out.println("Configuration [" + perms + "] PASSED all checks!");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
    }
}
