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

public class RenderLoginAndOverviewSnapshots {
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

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // 1. Render Login Form
                FXMLLoader loginLoader = new FXMLLoader(RenderLoginAndOverviewSnapshots.class.getResource("/view/user_login_form.fxml"));
                Parent loginRoot = loginLoader.load();
                Scene loginScene = new Scene(loginRoot, 940, 580);
                loginRoot.applyCss();
                loginRoot.layout();

                WritableImage loginSnap = loginRoot.snapshot(null, null);
                BufferedImage loginBImg = toBufferedImage(loginSnap);
                File loginOut = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/login_view_preview.png");
                ImageIO.write(loginBImg, "png", loginOut);
                System.out.println("SAVED: " + loginOut.getAbsolutePath());

                // 2. Render Overview View if exists
                try {
                    FXMLLoader overviewLoader = new FXMLLoader(RenderLoginAndOverviewSnapshots.class.getResource("/view/overview_view.fxml"));
                    Parent overviewRoot = overviewLoader.load();
                    Scene overviewScene = new Scene(overviewRoot, 1000, 650);
                    overviewRoot.applyCss();
                    overviewRoot.layout();

                    WritableImage overviewSnap = overviewRoot.snapshot(null, null);
                    BufferedImage overviewBImg = toBufferedImage(overviewSnap);
                    File overviewOut = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/overview_view_preview.png");
                    ImageIO.write(overviewBImg, "png", overviewOut);
                    System.out.println("SAVED: " + overviewOut.getAbsolutePath());
                } catch (Exception ex) {
                    System.out.println("Overview render skipped or failed: " + ex.getMessage());
                }

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }

    private static BufferedImage toBufferedImage(WritableImage snapshot) {
        BufferedImage bImg = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(), BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = snapshot.getPixelReader();
        for (int y = 0; y < (int) snapshot.getHeight(); y++) {
            for (int x = 0; x < (int) snapshot.getWidth(); x++) {
                bImg.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        return bImg;
    }
}
