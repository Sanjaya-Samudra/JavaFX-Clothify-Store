import controller.user.UserLoginFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Starter extends Application {

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/user_login_form.fxml")))));
        stage.show();
        stage.setResizable(false);
    }

}
