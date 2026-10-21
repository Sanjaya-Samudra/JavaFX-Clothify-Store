package test;

import controller.item.ItemFormController;
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
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RenderItemFormMaximizedSnapshots {

    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        User admin = new User("1", "one@gmail.com", "one", "Mr", "Danapala",
                LocalDate.of(1990, 1, 1), 75000.0, "Headquarters", "Colombo", "Western", "ADMIN", "ACTIVE", "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
        UserSession.getInstance().login(admin);

        Platform.runLater(() -> {
            try {
                // 1. Minimized / Normal View (960 x 640)
                FXMLLoader loaderMin = new FXMLLoader(RenderItemFormMaximizedSnapshots.class.getResource("/view/item_form.fxml"));
                Parent rootMin = loaderMin.load();
                ItemFormController ctrlMin = loaderMin.getController();
                Scene sceneMin = new Scene(rootMin, 960, 640);
                ctrlMin.updatePlaceholders(false);
                rootMin.applyCss();
                rootMin.layout();
                saveSnapshot(rootMin, "item_form_minimized_preview.png");

                // 1b. Minimized with empty inputs (placeholder view)
                ctrlMin.btnClearOnAction(null);
                rootMin.applyCss();
                rootMin.layout();
                saveSnapshot(rootMin, "item_form_minimized_empty_preview.png");

                // 2. Maximized / Large Screen View (1360 x 760)
                FXMLLoader loaderMax = new FXMLLoader(RenderItemFormMaximizedSnapshots.class.getResource("/view/item_form.fxml"));
                Parent rootMax = loaderMax.load();
                ItemFormController ctrlMax = loaderMax.getController();
                Scene sceneMax = new Scene(rootMax, 1360, 760);
                ctrlMax.updatePlaceholders(true);
                rootMax.applyCss();
                rootMax.layout();
                saveSnapshot(rootMax, "item_form_maximized_preview.png");

                // 2b. Maximized with empty inputs (expanded placeholder view)
                ctrlMax.btnClearOnAction(null);
                rootMax.applyCss();
                rootMax.layout();
                saveSnapshot(rootMax, "item_form_maximized_empty_preview.png");

                // 3. Order History Minimized (1120 x 720)
                FXMLLoader loaderOrdMin = new FXMLLoader(RenderItemFormMaximizedSnapshots.class.getResource("/view/order_history_view.fxml"));
                Parent rootOrdMin = loaderOrdMin.load();
                Scene sceneOrdMin = new Scene(rootOrdMin, 1120, 720);
                rootOrdMin.applyCss();
                rootOrdMin.layout();
                saveSnapshot(rootOrdMin, "order_history_minimized_preview.png");

                // 4. Order History Maximized (1600 x 900)
                FXMLLoader loaderOrdMax = new FXMLLoader(RenderItemFormMaximizedSnapshots.class.getResource("/view/order_history_view.fxml"));
                Parent rootOrdMax = loaderOrdMax.load();
                Scene sceneOrdMax = new Scene(rootOrdMax, 1600, 900);
                rootOrdMax.applyCss();
                rootOrdMax.layout();
                saveSnapshot(rootOrdMax, "order_history_maximized_preview.png");

                System.out.println("ALL_ITEM_FORM_SNAPSHOTS_RENDERED_SUCCESSFULLY");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
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
