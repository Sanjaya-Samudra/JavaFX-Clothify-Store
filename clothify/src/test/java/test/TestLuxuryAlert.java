package test;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public class TestLuxuryAlert {
    public static void main(String[] args) throws Exception {
        CountDownLatch startup = new CountDownLatch(1);
        try { Platform.startup(startup::countDown); } catch (Exception e) { startup.countDown(); }
        startup.await();
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Clothify Store");
                alert.setGraphic(null);

                DialogPane pane = alert.getDialogPane();
                pane.setGraphic(null);
                pane.getButtonTypes().add(ButtonType.OK);
                pane.setStyle("-fx-background-color: #ffffff; -fx-padding: 24; -fx-background-radius: 16; -fx-border-color: #e2e8f0; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 6);");

                HBox contentBox = new HBox(18);
                contentBox.setAlignment(Pos.TOP_LEFT);

                // Luxury Emerald SVG Checkmark Badge
                StackPane badge = new StackPane();
                badge.setPrefSize(52, 52);
                Circle circle = new Circle(26);
                circle.setFill(Color.web("#ecfdf5"));
                circle.setStroke(Color.web("#a7f3d0"));
                circle.setStrokeWidth(1.5);

                SVGPath checkmark = new SVGPath();
                checkmark.setContent("M 16 26 L 22 32 L 34 18");
                checkmark.setStroke(Color.web("#059669"));
                checkmark.setStrokeWidth(3.0);
                checkmark.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                checkmark.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
                checkmark.setFill(Color.TRANSPARENT);
                badge.getChildren().addAll(circle, checkmark);

                VBox textBox = new VBox(6);
                HBox.setHgrow(textBox, Priority.ALWAYS);

                Label lblTitle = new Label("Inventory Restocked");
                lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                lblTitle.setTextFill(Color.web("#0f172a"));

                Label lblMessage = new Label("Silk Evening Gown was successfully updated with 20 units added to stock.");
                lblMessage.setFont(Font.font("Segoe UI", 13.5));
                lblMessage.setTextFill(Color.web("#475569"));
                lblMessage.setWrapText(true);
                lblMessage.setMaxWidth(380);

                textBox.getChildren().addAll(lblTitle, lblMessage);
                contentBox.getChildren().addAll(badge, textBox);
                pane.setContent(contentBox);

                Button okButton = (Button) pane.lookupButton(ButtonType.OK);
                if (okButton != null) {
                    okButton.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 26; -fx-cursor: hand;");
                    okButton.setText("Got it");
                }

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

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/alert_luxury_preview.png");
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
