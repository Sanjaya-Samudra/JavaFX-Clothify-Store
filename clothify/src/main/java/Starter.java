import db.DBInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class Starter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Automatically initialize database tables and seed accounts
        try {
            DBInitializer.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database auto-init note: " + e.getMessage());
        }

        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user_login_form.fxml"))));
        stage.setScene(scene);
        stage.setTitle("Clothify Store - Fashion POS & ERP Management System");

        try {
            InputStream iconStream = getClass().getResourceAsStream("/Images/logo-background-removed-nav.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) {}

        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });
        util.WindowUtil.centerInVisualBounds(stage, 980, 620);
        stage.show();
    }
}
