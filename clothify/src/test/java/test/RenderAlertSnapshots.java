package test;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import util.AlertUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RenderAlertSnapshots {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // We want to capture the DialogPane created by AlertUtil
                // Let's create an Alert in the exact same way AlertUtil does
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Clothify — Item Added Successfully");
                alert.setHeaderText(null);

                // Use reflection to test createIconBadge and dialog building
                DialogPane pane = alert.getDialogPane();
                pane.setStyle("-fx-background-color: #ffffff; -fx-padding: 16;");

                // Let's trigger AlertUtil.showSuccess
                // Note: showSuccess calls alert.show() which is non-blocking!
                AlertUtil.showSuccess("Inventory Restocked", "Silk Evening Gown was successfully updated with 20 units added to stock.");

                // Let's also create an alert directly and snapshot its dialog pane
                pane.applyCss();
                pane.layout();

                WritableImage snap = pane.snapshot(null, null);
                BufferedImage bImg = new BufferedImage((int) snap.getWidth(), (int) snap.getHeight(), BufferedImage.TYPE_INT_ARGB);
                PixelReader reader = snap.getPixelReader();
                for (int y = 0; y < (int) snap.getHeight(); y++) {
                    for (int x = 0; x < (int) snap.getWidth(); x++) {
                        bImg.setRGB(x, y, reader.getArgb(x, y));
                    }
                }

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/alert_preview.png");
                ImageIO.write(bImg, "png", outFile);
                System.out.println("SAVED: " + outFile.getAbsolutePath());
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }
}
