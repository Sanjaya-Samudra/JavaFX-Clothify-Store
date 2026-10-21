package test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.User;
import security.UserSession;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestTableLayout {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await(5, TimeUnit.SECONDS);

        User admin = new User("1", "one@gmail.com", "one", "Mr", "Danapala",
                LocalDate.of(1990, 1, 1), 75000.0, "Headquarters", "Colombo", "Western", "ADMIN", "ACTIVE", "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
        UserSession.getInstance().login(admin);

        Platform.runLater(() -> {
            try {
                // Minimized (960 x 640)
                FXMLLoader l1 = new FXMLLoader(TestTableLayout.class.getResource("/view/item_form.fxml"));
                Parent r1 = l1.load();
                Scene s1 = new Scene(r1, 960, 640);
                TableView<?> t1 = (TableView<?>) r1.lookup("#tblItems");
                applyColumnConstraints(t1);
                r1.applyCss();
                r1.layout();
                System.out.println("--- MINIMIZED 960x640 ---");
                System.out.println("Table size = " + t1.getWidth() + " x " + t1.getHeight());
                t1.getColumns().forEach(c -> System.out.println("  Col [" + c.getText() + "] width = " + c.getWidth()));

                // Maximized (1920 x 1080)
                FXMLLoader l2 = new FXMLLoader(TestTableLayout.class.getResource("/view/item_form.fxml"));
                Parent r2 = l2.load();
                Scene s2 = new Scene(r2, 1920, 1080);
                TableView<?> t2 = (TableView<?>) r2.lookup("#tblItems");
                applyColumnConstraints(t2);
                r2.applyCss();
                r2.layout();
                System.out.println("--- MAXIMIZED 1920x1080 ---");
                System.out.println("Table size = " + t2.getWidth() + " x " + t2.getHeight());
                t2.getColumns().forEach(c -> System.out.println("  Col [" + c.getText() + "] width = " + c.getWidth()));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
    }

    private static void applyColumnConstraints(TableView<?> t) {
        for (TableColumn<?, ?> col : t.getColumns()) {
            String text = col.getText();
            if ("Item Code".equals(text)) {
                col.setMinWidth(75);
                col.setPrefWidth(85);
                col.setMaxWidth(95);
            } else if ("Size".equals(text)) {
                col.setMinWidth(45);
                col.setPrefWidth(55);
                col.setMaxWidth(65);
            } else if ("Quantity".equals(text)) {
                col.setMinWidth(55);
                col.setPrefWidth(65);
                col.setMaxWidth(80);
            } else if ("Price (LKR)".equals(text)) {
                col.setMinWidth(95);
                col.setPrefWidth(105);
                col.setMaxWidth(130);
            } else if ("Category".equals(text)) {
                col.setMinWidth(85);
                col.setPrefWidth(100);
                col.setMaxWidth(130);
            } else if ("Supplier".equals(text)) {
                col.setMinWidth(140);
                col.setPrefWidth(170);
                col.setMaxWidth(280);
            } else if ("Image Status".equals(text)) {
                col.setMinWidth(90);
                col.setPrefWidth(100);
                col.setMaxWidth(115);
            } else if ("Apparel Name".equals(text)) {
                col.setMinWidth(220);
                col.setPrefWidth(300);
                col.setMaxWidth(10000);
            }
        }
    }
}
