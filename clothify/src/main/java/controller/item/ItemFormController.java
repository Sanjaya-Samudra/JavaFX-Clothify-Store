package controller.item;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Item;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ItemFormController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbCategory;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colImage;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colPrice;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private TableColumn<?, ?> colSize;

    @FXML
    private TableColumn<?, ?> colSupplier;

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
    private TableView<Item> tblItems;

    ItemService service = ItemController.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        ObservableList<String> titleList = FXCollections.observableArrayList();
        titleList.add("Kids");
        titleList.add("Adult");
        cmbCategory.setItems(titleList);

        tblItems.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null) {
                addValueToText(newVal);
            }
        });

        loadTable();

    }

    private void addValueToText(Item newVal) {
        txtItemCode.setText(newVal.getItemCode());
        txtName.setText(newVal.getName());
        txtQuantity.setText(newVal.getQuantity().toString());
        txtPrice.setText(newVal.getPrice().toString());
        txtSize.setText(newVal.getSize());
        cmbCategory.setValue(newVal.getCategory());
        txtSupplier.setText(newVal.getSupplier());
        imageName.setText(newVal.getImage().toString());
    }

    private void loadTable() {
        tblItems.setItems(service.getAllItems());
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if(
                service.addItem(new Item(
                        txtItemCode.getText(),
                        txtName.getText(),
                        txtSize.getText(),
                        Double.parseDouble(txtPrice.getText()),
                        Integer.parseInt(txtQuantity.getText()),
                        cmbCategory.getValue(),
                        txtSupplier.getText(),
                        imageName.getText().getBytes()
                ))
        ){
            new Alert(Alert.AlertType.INFORMATION, "Item Added!").show();
            loadTable();
        } else {
            new Alert(Alert.AlertType.ERROR, "Item Not Added!").show();
        }
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
        txtSupplier.setText("");
        txtSize.setText("");
        txtName.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
        txtItemCode.setText("");
        imageName.setText("");
        cmbCategory.setValue("");
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        if (service.deleteItem(txtItemCode.getText())) {
            new Alert(Alert.AlertType.INFORMATION, "Item Deleted!!").show();
            loadTable();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Item Not Deleted!!").show();
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

}
