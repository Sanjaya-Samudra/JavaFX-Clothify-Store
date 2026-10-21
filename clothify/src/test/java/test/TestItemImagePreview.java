package test;

import controller.item.ItemFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestItemImagePreview {
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
                FXMLLoader loader = new FXMLLoader(TestItemImagePreview.class.getResource("/view/item_form.fxml"));
                Parent root = loader.load();
                ItemFormController controller = loader.getController();

                // Find itemImage
                ImageView itemImage = (ImageView) root.lookup("#itemImage");
                File sampleImgFile = new File("clothify/src/main/resources/Images/logo-background-removed-nav.png");
                if (!sampleImgFile.exists()) {
                    sampleImgFile = new File("src/main/resources/Images/logo-background-removed-nav.png");
                }
                System.out.println("Sample image exists: " + sampleImgFile.exists());
                Image sampleImg = new Image(new FileInputStream(sampleImgFile));
                itemImage.setImage(sampleImg);

                Scene scene = new Scene(root, 960, 640);
                root.applyCss();
                root.layout();

                WritableImage snapshot = root.snapshot(null, null);
                BufferedImage bImg = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(), BufferedImage.TYPE_INT_ARGB);
                PixelReader reader = snapshot.getPixelReader();
                for (int y = 0; y < (int) snapshot.getHeight(); y++) {
                    for (int x = 0; x < (int) snapshot.getWidth(); x++) {
                        bImg.setRGB(x, y, reader.getArgb(x, y));
                    }
                }

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/item_image_with_upload_preview.png");
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
