package controller.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class DashboardFormController {

    @FXML
    void btnEmployeeManagementOnAction(ActionEvent event) {

    }

    @FXML
    void btnOrderManagementOnAction(ActionEvent event) {

    }

    @FXML
    void btnProductManagementOnAction(ActionEvent event) {

    }

    @FXML
    void btnReportManagementOnAction(ActionEvent event) {

    }

    @FXML
    void btnSignOutOnAction(ActionEvent event) {

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Sign Out");
        alert.setHeaderText("Are you sure you want to sign out?");
        alert.setContentText("Press OK to log out or Cancel to stay.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage stage = new Stage();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../../view/user_login_form.fxml"))));
                stage.show();
                currentStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // User clicked Cancel - do nothing
        }
    }

    @FXML
    void btnSupplierManagementOnAction(ActionEvent event) {

    }

    @FXML
    void btnUserManagementOnAction(ActionEvent event) {

    }

}
