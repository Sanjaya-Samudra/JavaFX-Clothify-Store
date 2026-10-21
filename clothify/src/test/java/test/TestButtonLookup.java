package test;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import java.util.concurrent.CountDownLatch;

public class TestButtonLookup {
    public static void main(String[] args) throws Exception {
        CountDownLatch startup = new CountDownLatch(1);
        try { Platform.startup(startup::countDown); } catch (Exception e) { startup.countDown(); }
        startup.await();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            Button btnBefore = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            System.out.println("Button before show/layout: " + btnBefore);
            alert.getDialogPane().applyCss();
            alert.getDialogPane().layout();
            Button btnAfterLayout = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            System.out.println("Button after applyCss/layout: " + btnAfterLayout);
            System.exit(0);
        });
    }
}
