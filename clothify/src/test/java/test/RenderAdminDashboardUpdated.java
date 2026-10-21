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

public class RenderAdminDashboardUpdated {
    public static void main(String[] args) throws Exception {
        CountDownLatch startup = new CountDownLatch(1);
        try { Platform.startup(startup::countDown); } catch (Exception e) { startup.countDown(); }
        startup.await();

        User admin = new User("1", "one@gmail.com", "one", "Mr", "Danapala",
                LocalDate.of(1990, 1, 1), 75000.0, "Headquarters", "Colombo", "Western", "ADMIN", "ACTIVE", "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
        UserSession.getInstance().login(admin);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(RenderAdminDashboardUpdated.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1200, 700);
                root.applyCss();
                root.layout();

                WritableImage snap = root.snapshot(null, null);
                BufferedImage bImg = new BufferedImage((int) snap.getWidth(), (int) snap.getHeight(), BufferedImage.TYPE_INT_ARGB);
                PixelReader reader = snap.getPixelReader();
                for (int y = 0; y < (int) snap.getHeight(); y++) {
                    for (int x = 0; x < (int) snap.getWidth(); x++) {
                        bImg.setRGB(x, y, reader.getArgb(x, y));
                    }
                }

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/dashboard_admin_verified.png");
                ImageIO.write(bImg, "png", outFile);
                System.out.println("SAVED: " + outFile.getAbsolutePath());
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
    }
}
