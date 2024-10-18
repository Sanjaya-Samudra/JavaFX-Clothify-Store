package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private JFXTextField txtUserEmail;

    @FXML
    private JFXPasswordField txtUserPassword;

    @FXML
    void btnClickOnActionAdminLogin(ActionEvent event) {

    }

    @FXML
    void btnClickOnActionUserLogin(ActionEvent event) {
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/user_form.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void createAccountOnAction(MouseEvent event) {

    }

    @FXML
    void fogotPasswordOnAction(MouseEvent event) {

    }

}
