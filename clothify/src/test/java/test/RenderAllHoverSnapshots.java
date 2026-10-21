package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
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

public class RenderAllHoverSnapshots {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        // Admin session with all 6 modules: ORDER, PRODUCT, EMPLOYEE, REPORT, SUPPLIER, USER
        User admin = new User("1", "one@gmail.com", "one", "Mr", "Danapala",
                LocalDate.of(1990, 1, 1), 75000.0, "Headquarters", "Colombo", "Western", "ADMIN", "ACTIVE",
                "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
        UserSession.getInstance().login(admin);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(RenderAllHoverSnapshots.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController controller = loader.getController();

                Scene scene = new Scene(root, 1280, 780);
                root.applyCss();
                root.layout();

                Field fWheel = DashboardFormController.class.getDeclaredField("svgWheelGroup");
                fWheel.setAccessible(true);
                Group wheelGroup = (Group) fWheel.get(controller);

                saveSnapshot(root, "nav_hover_idle.png");

                // Hover each sector:
                // Module 0: ORDER
                // Module 1: PRODUCT
                // Module 2: EMPLOYEE
                // Module 3: REPORT
                // Module 4: SUPPLIER
                // Module 5: USER
                String[] names = {"order", "product", "employee", "report", "supplier", "user"};
                for (int i = 0; i < wheelGroup.getChildren().size() && i < names.length; i++) {
                    fireHover(wheelGroup, i);
                    root.applyCss();
                    root.layout();
                    saveSnapshot(root, "nav_hover_" + names[i] + ".png");
                    fireExit(wheelGroup, i);
                    root.applyCss();
                    root.layout();
                }

                System.out.println("ALL_HOVER_SNAPSHOTS_SAVED_SUCCESSFULLY");
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
