package test;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestNavOrbIcons {
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
                HBox root = new HBox(20);
                root.setAlignment(Pos.CENTER);
                root.setStyle("-fx-background-color: #f8fafc; -fx-padding: 30;");

                String[] keys = {"ORDER", "PRODUCT", "REPORT", "EMPLOYEE", "SUPPLIER", "USER"};
                String[] titles = {"Order Management", "Product Catalog", "Store Reports", "Staff Roster", "Supplier Network", "User Security"};

                for (int i = 0; i < keys.length; i++) {
                    VBox col = new VBox(10);
                    col.setAlignment(Pos.CENTER);
                    StackPane badge = createBadge(keys[i]);
                    Label lbl = new Label(titles[i]);
                    lbl.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #334155;");
                    col.getChildren().addAll(badge, lbl);
                    root.getChildren().add(col);
                }

                Scene scene = new Scene(root, 780, 160);
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

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/nav_orb_icons_test.png");
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

    private static StackPane createBadge(String moduleKey) {
        StackPane badge = new StackPane();
        badge.setPrefSize(56, 56);
        badge.setMinSize(56, 56);
        badge.setMaxSize(56, 56);

        Circle circle = new Circle(28);
        circle.setStrokeWidth(1.8);

        SVGPath iconPath = new SVGPath();
        iconPath.setStrokeWidth(2.0);
        iconPath.setStrokeLineCap(StrokeLineCap.ROUND);
        iconPath.setStrokeLineJoin(StrokeLineJoin.ROUND);
        iconPath.setFill(Color.TRANSPARENT);

        String strokeColor = "#d97706";
        String fillColor = "#fffbeb";
        String borderColor = "#fde68a";
        String pathData = "";

        switch (moduleKey) {
            case "ORDER":
                // Luxury Boutique Shopping Bag with handle and crease
                fillColor = "#fdf2f8";
                borderColor = "#fbcfe8";
                strokeColor = "#db2777";
                pathData = "M 6 2 L 3 6 L 3 20 C 3 21.1 3.9 22 5 22 L 19 22 C 20.1 22 21 21.1 21 20 L 21 6 L 18 2 Z M 3 6 L 21 6 M 16 10 C 16 12.2 14.2 14 12 14 C 9.8 14 8 12.2 8 10";
                break;
            case "PRODUCT":
                // Haute Couture Tailored Shirt with collar and cuffs
                fillColor = "#fffbeb";
                borderColor = "#fde68a";
                strokeColor = "#d97706";
                pathData = "M 20.38 3.46 L 16 2 C 14.5 3.5 9.5 3.5 8 2 L 3.62 3.46 C 2.65 3.7 2 4.6 2 5.6 L 2 10 C 2 11.1 2.9 12 4 12 L 6 12 L 6 21 C 6 21.6 6.4 22 7 22 L 17 22 C 17.6 22 18 21.6 18 21 L 18 12 L 20 12 C 21.1 12 22 11.1 22 10 L 22 5.6 C 22 4.6 21.35 3.7 20.38 3.46 Z M 16 2 C 14.5 4 9.5 4 8 2 M 12 5 L 12 22";
                break;
            case "REPORT":
                // Analytics growth chart with upward trendline & arrow
                fillColor = "#ecfdf5";
                borderColor = "#a7f3d0";
                strokeColor = "#059669";
                pathData = "M 3 3 L 3 20 C 3 20.6 3.4 21 4 21 L 21 21 M 7 17 L 7 13 M 12 17 L 12 9 M 17 17 L 17 5 M 6 11 L 11 7 L 15 10 L 20 4 M 16 4 L 20 4 L 20 8";
                break;
            case "EMPLOYEE":
                // Boutique Staff & Atelier Team (2 members)
                fillColor = "#f5f3ff";
                borderColor = "#ddd6fe";
                strokeColor = "#7c3aed";
                pathData = "M 16 21 L 16 19 C 16 16.8 14.2 15 12 15 L 6 15 C 3.8 15 2 16.8 2 19 L 2 21 M 9 11 C 11.2 11 13 9.2 13 7 C 13 4.8 11.2 3 9 3 C 6.8 3 5 4.8 5 7 C 5 9.2 6.8 11 9 11 Z M 16 3.1 C 17.8 3.6 19 5.1 19 7 C 19 8.9 17.8 10.4 16 10.9 M 22 21 L 22 19 C 22 17.2 20.8 15.7 19.1 15.2";
                break;
            case "SUPPLIER":
                // Wholesale delivery truck / textile supplier
                fillColor = "#f0f9ff";
                borderColor = "#bae6fd";
                strokeColor = "#0284c7";
                pathData = "M 1 4 L 14 4 L 14 16 L 1 16 Z M 14 8 L 19 8 L 22 11 L 22 16 L 14 16 M 1 16 L 3.5 16 M 7.5 16 L 15.5 16 M 19.5 16 L 22 16 M 5.5 18 A 2 2 0 1 0 5.5 14 A 2 2 0 0 0 5.5 18 Z M 17.5 18 A 2 2 0 1 0 17.5 14 A 2 2 0 0 0 17.5 18 Z";
                break;
            case "USER":
                // Authenticated user security shield
                fillColor = "#eff6ff";
                borderColor = "#bfdbfe";
                strokeColor = "#2563eb";
                pathData = "M 12 22 C 12 22 20 18 20 12 L 20 5 L 12 2 L 4 5 L 4 12 C 4 18 12 22 12 22 Z M 12 11 C 13.1 11 14 10.1 14 9 C 14 7.9 13.1 7 12 7 C 10.9 7 10 7.9 10 9 C 10 10.1 10.9 11 12 11 Z M 8.5 16.5 C 8.5 15 10 14 12 14 C 14 14 15.5 15 15.5 16.5";
                break;
        }

        circle.setFill(Color.web(fillColor));
        circle.setStroke(Color.web(borderColor));
        iconPath.setContent(pathData);
        iconPath.setStroke(Color.web(strokeColor));
        iconPath.setScaleX(1.35);
        iconPath.setScaleY(1.35);

        badge.getChildren().addAll(circle, iconPath);
        return badge;
    }
}
