package test;

import controller.dashboard.DashboardFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Item;
import model.User;
import security.UserSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RenderStockoutItemsTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch startup = new CountDownLatch(1);
        try { Platform.startup(startup::countDown); } catch (Exception e) { startup.countDown(); }
        startup.await();

        User admin = new User("1", "one@gmail.com", "one", "Mr", "Danapala",
                LocalDate.of(1990, 1, 1), 75000.0, "Headquarters", "Colombo", "Western", "ADMIN", "ACTIVE", "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
        UserSession.getInstance().login(admin);

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(RenderStockoutItemsTest.class.getResource("/view/dashboard_form.fxml"));
                Parent root = loader.load();
                DashboardFormController ctrl = loader.getController();

                // Access private fields via reflection to simulate stockout items
                Field stockoutListField = DashboardFormController.class.getDeclaredField("vboxStockoutList");
                stockoutListField.setAccessible(true);
                VBox vbox = (VBox) stockoutListField.get(ctrl);

                Field stockoutBadgeField = DashboardFormController.class.getDeclaredField("lblStockoutListBadge");
                stockoutBadgeField.setAccessible(true);
                Label badge = (Label) stockoutBadgeField.get(ctrl);

                Field stockoutCountField = DashboardFormController.class.getDeclaredField("lblStockOut");
                stockoutCountField.setAccessible(true);
                Label stockOutLbl = (Label) stockoutCountField.get(ctrl);

                List<Item> simulatedStockouts = new ArrayList<>();
                simulatedStockouts.add(new Item("P003", "Velvet Evening Blazer", "M", 28500.0, 0, "Suits", "S001", null));
                simulatedStockouts.add(new Item("P007", "Pure Silk Necktie Navy", "Standard", 4200.0, 0, "Accessories", "S002", null));
                simulatedStockouts.add(new Item("P009", "Cashmere Wool Cardigan", "L", 18900.0, 0, "Knitwear", "S003", null));

                stockOutLbl.setText(simulatedStockouts.size() + " Out");
                badge.setText(simulatedStockouts.size() + " Out");
                badge.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-padding: 1 6; -fx-background-radius: 8; -fx-font-size: 9px; -fx-font-weight: bold;");

                vbox.getChildren().clear();
                for (Item it : simulatedStockouts) {
                    HBox row = new HBox(6);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-background-color: #ffffff; -fx-padding: 4 6; -fx-background-radius: 6; -fx-border-color: #fecaca; -fx-border-radius: 6; -fx-cursor: hand;");

                    Label codeBadge = new Label(it.getItemCode());
                    codeBadge.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-background-color: #fef2f2; -fx-padding: 1 4; -fx-background-radius: 4; -fx-border-color: #fca5a5; -fx-border-radius: 4;");

                    Label nameLbl = new Label(it.getName());
                    nameLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #0f172a; -fx-font-weight: bold;");
                    HBox.setHgrow(nameLbl, Priority.ALWAYS);
                    nameLbl.setMaxWidth(110.0);
                    nameLbl.setTextOverrun(OverrunStyle.ELLIPSIS);

                    Label qtyBadge = new Label("0 Qty");
                    qtyBadge.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

                    row.getChildren().addAll(codeBadge, nameLbl, qtyBadge);
                    vbox.getChildren().add(row);
                }

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

                File outFile = new File("C:/Users/Sanjaya/.gemini/antigravity/brain/32284aee-0ae5-41b0-82ee-a5d884f01f2c/dashboard_admin_stockouts_simulated.png");
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
