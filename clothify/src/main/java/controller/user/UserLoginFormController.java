package controller.user;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;


public class UserLoginFormController {

    @FXML
    private JFXTextField email;

    @FXML
    private JFXCheckBox forgotPassword;

    @FXML
    private ImageView loginImage;

    @FXML
    private JFXTextField password;

    @FXML
    private JPanel loginPane;

    UserService service = UserController.getInstance();

    public void btnLoginOnAction(javafx.event.ActionEvent actionEvent) {
        if (service.verifyLogin(email.getText(), password.getText())) {
            new Alert(Alert.AlertType.INFORMATION, "User Login Successfully").show();
            try {
                Stage stage = new Stage();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../../view/dashboard_form.fxml"))));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "User Login Not Successful").show();
        }
    }
}
