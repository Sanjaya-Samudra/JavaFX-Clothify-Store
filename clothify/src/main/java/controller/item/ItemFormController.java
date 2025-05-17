package controller.item;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class ItemFormController {

    @FXML
    private JFXComboBox<?> cmbCategory;

    @FXML
    private TextField imageName;

    @FXML
    private ImageView itemImage;

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private JFXTextField txtQuantity;

    @FXML
    private JFXTextField txtSize;

    @FXML
    private JFXTextField txtSupplier;

    @FXML
    void btnAddOnAction(ActionEvent event) {

    }

    @FXML
    void btnBrowseOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All images", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // 1. Show file name in label
            imageName.setText(selectedFile.getName());

            // 2. Load image and show in ImageView
            Image image = new Image(selectedFile.toURI().toString());
            itemImage.setImage(image);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

}
