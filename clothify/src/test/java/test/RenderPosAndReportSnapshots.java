package test;

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

public class RenderPosAndReportSnapshots {

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
                // 1. POS View
                FXMLLoader posLoader = new FXMLLoader(RenderPosAndReportSnapshots.class.getResource("/view/pos_order_view.fxml"));
                Parent posRoot = posLoader.load();
                Scene posScene = new Scene(posRoot, 1100, 720);
                posRoot.applyCss();
                posRoot.layout();
                saveSnapshot(posRoot, "pos_view_luxury_preview.png");

                // 2. Report View
                FXMLLoader reportLoader = new FXMLLoader(RenderPosAndReportSnapshots.class.getResource("/view/report_view.fxml"));
                Parent reportRoot = reportLoader.load();
                Scene reportScene = new Scene(reportRoot, 1100, 700);
                reportRoot.applyCss();
                reportRoot.layout();
                saveSnapshot(reportRoot, "report_view_luxury_preview.png");

                System.out.println("ALL_POS_AND_REPORT_RENDERED");
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
