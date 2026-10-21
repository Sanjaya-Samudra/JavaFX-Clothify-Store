package test;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import util.AlertUtil;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public class RenderAllAlerts {
    public static void main(String[] args) throws Exception {
        CountDownLatch startup = new CountDownLatch(1);
        try { Platform.startup(startup::countDown); } catch (Exception e) { startup.countDown(); }
        startup.await();
        Platform.runLater(() -> {
            try {
                // Success
                AlertUtil.showSuccess("Item Restocked", "Silk Evening Gown was successfully updated with 20 units added to stock.");
                // Error
                AlertUtil.showError("Connection Failed", "Unable to establish handshake with MySQL server. Please check port 3306.");
                // Warning
                AlertUtil.showWarning("Low Stock Warning", "Only 2 units of Linen Blazer remaining in stock. Consider reordering.");
                
                System.out.println("Alerts launched successfully.");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
    }
}
