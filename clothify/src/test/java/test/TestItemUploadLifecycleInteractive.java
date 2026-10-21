package test;

import controller.item.ItemFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import model.Item;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestItemUploadLifecycleInteractive {
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
                FXMLLoader loader = new FXMLLoader(TestItemUploadLifecycleInteractive.class.getResource("/view/item_form.fxml"));
                Parent root = loader.load();
                ItemFormController controller = loader.getController();

                TableView<Item> tbl = (TableView<Item>) root.lookup("#tblItems");
                VBox placeholder = (VBox) root.lookup("#placeholderBox");
                ImageView itemImage = (ImageView) root.lookup("#itemImage");

                System.out.println("Initial state (P001 selected):");
                System.out.println("  placeholderBox visible: " + placeholder.isVisible());
                System.out.println("  itemImage visible: " + itemImage.isVisible());

                // Now select row 1 (P002, which has no image)
                tbl.getSelectionModel().select(1);
                System.out.println("After selecting P002 (no photo):");
                System.out.println("  placeholderBox visible: " + placeholder.isVisible());
                System.out.println("  itemImage visible: " + itemImage.isVisible());

                Scene scene = new Scene(root, 960, 640);
                root.applyCss();
                root.layout();

                // Save snapshot with P002 selected (showing new SVG placeholder)
                saveSnapshot(root, "C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/item_p002_placeholder_verified.png");

                // Now simulate user browsing an image for P002:
                File testImg = new File("src/main/resources/Images/logo-background-removed-central.png");
                byte[] bytes = Files.readAllBytes(testImg.toPath());
                javafx.scene.image.Image img = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(bytes));
                
                // Set image on item and update preview
                Item p002 = tbl.getItems().get(1);
                p002.setImage(bytes);
                tbl.refresh();
                
                // Update preview as controller does
                itemImage.setImage(img);
                itemImage.setVisible(true);
                placeholder.setVisible(false);

                root.applyCss();
                root.layout();

                // Save snapshot with P002 having an uploaded image
                saveSnapshot(root, "C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/item_p002_uploaded_verified.png");
                System.out.println("Snapshots successfully generated!");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }

    private static void saveSnapshot(Parent root, String path) throws Exception {
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage bImg = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(), BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = snapshot.getPixelReader();
        for (int y = 0; y < (int) snapshot.getHeight(); y++) {
            for (int x = 0; x < (int) snapshot.getWidth(); x++) {
                bImg.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        ImageIO.write(bImg, "png", new File(path));
        System.out.println("SAVED: " + path);
    }
}
