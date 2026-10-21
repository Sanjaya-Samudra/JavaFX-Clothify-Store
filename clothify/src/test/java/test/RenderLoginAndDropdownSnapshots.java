package test;

import controller.user.UserLoginFormController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import model.User;
import security.UserSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RenderLoginAndDropdownSnapshots {

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
                // 1. Render Default Sign In (Admin selected)
                FXMLLoader loginLoader1 = new FXMLLoader(RenderLoginAndDropdownSnapshots.class.getResource("/view/user_login_form.fxml"));
                Parent loginRoot1 = loginLoader1.load();
                Scene loginScene1 = new Scene(loginRoot1, 980, 620);
                loginRoot1.applyCss();
                loginRoot1.layout();
                saveSnapshot(loginRoot1, "login_view_preview.png");

                // 2. Render Sign In with Cashier selected
                FXMLLoader loginLoader2 = new FXMLLoader(RenderLoginAndDropdownSnapshots.class.getResource("/view/user_login_form.fxml"));
                Parent loginRoot2 = loginLoader2.load();
                UserLoginFormController ctrl2 = loginLoader2.getController();
                Scene loginScene2 = new Scene(loginRoot2, 980, 620);
                Method cashierSelect = UserLoginFormController.class.getDeclaredMethod("onRoleCashierSelected", javafx.event.ActionEvent.class);
                cashierSelect.setAccessible(true);
                cashierSelect.invoke(ctrl2, (Object) null);
                loginRoot2.applyCss();
                loginRoot2.layout();
                saveSnapshot(loginRoot2, "login_view_cashier_preview.png");

                // 3. Render Forgot Password View (Inline)
                FXMLLoader loginLoader3 = new FXMLLoader(RenderLoginAndDropdownSnapshots.class.getResource("/view/user_login_form.fxml"));
                Parent loginRoot3 = loginLoader3.load();
                UserLoginFormController ctrl3 = loginLoader3.getController();
                Scene loginScene3 = new Scene(loginRoot3, 980, 620);
                Method forgotSwitch = UserLoginFormController.class.getDeclaredMethod("onSwitchToForgotPassword", javafx.event.ActionEvent.class);
                forgotSwitch.setAccessible(true);
                forgotSwitch.invoke(ctrl3, (Object) null);
                loginRoot3.applyCss();
                loginRoot3.layout();
                saveSnapshot(loginRoot3, "login_forgot_password_preview.png");

                // 4. Render Register View (Inline)
                FXMLLoader loginLoader4 = new FXMLLoader(RenderLoginAndDropdownSnapshots.class.getResource("/view/user_login_form.fxml"));
                Parent loginRoot4 = loginLoader4.load();
                UserLoginFormController ctrl4 = loginLoader4.getController();
                Scene loginScene4 = new Scene(loginRoot4, 980, 620);
                Method regSwitch = UserLoginFormController.class.getDeclaredMethod("onSwitchToRegister", javafx.event.ActionEvent.class);
                regSwitch.setAccessible(true);
                regSwitch.invoke(ctrl4, (Object) null);
                loginRoot4.applyCss();
                loginRoot4.layout();
                saveSnapshot(loginRoot4, "login_register_preview.png");

                // 5. Render View with Dropdowns (Item Management View)
                FXMLLoader itemLoader = new FXMLLoader(RenderLoginAndDropdownSnapshots.class.getResource("/view/item_management_view.fxml"));
                Parent itemRoot = itemLoader.load();
                Scene itemScene = new Scene(itemRoot, 1050, 700);
                itemRoot.applyCss();
                itemRoot.layout();
                saveSnapshot(itemRoot, "dropdowns_item_management_preview.png");

                System.out.println("ALL_LOGIN_AND_DROPDOWN_SNAPSHOTS_RENDERED");
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
