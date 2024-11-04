package controller.item;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dto.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static sun.net.www.MimeTable.loadTable;

public class ItemFormController implements Initializable {


    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private ComboBox<String> cmbItemCategory;

    @FXML
    private ComboBox<String> cmbItemSupplierName;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colPrice;

    @FXML
    private TableColumn<?, ?> colSize;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private TableColumn<?, ?> colStock;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colSupplierName;

    @FXML
    private TableView<Item> tblItems;

    @FXML
    private JFXTextField txtSize;

    @FXML
    private JFXTextField txtQuantity;

    @FXML
    private JFXTextField txtStock;

    ItemService service = ItemController.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadCategory();
        loadSuplierName();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("size"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        tblItems.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null) {
                addValueToText(newVal);
            }
        });

        loadTable();
    }

    private void addValueToText(Item newVal) {
        txtId.setText(newVal.getId());
        txtName.setText(newVal.getName());
        txtSize.setText(newVal.getSize());
        txtPrice.setText(newVal.getPrice().toString());
        txtQuantity.setText(newVal.getQuantity().toString());
        txtStock.setText(newVal.getStock().toString());
    }

    private void loadCategory() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        ObservableList<Item> allItems = ItemController.getInstance().getAllItems();

        allItems.forEach(obj->{
            categoryList.add(obj.getId());
        });

        cmbItemCategory.setItems(categoryList);
    }

    private void loadSuplierName() {
        ObservableList<String> supplierNameList = FXCollections.observableArrayList();
        ObservableList<Item> allItems = ItemController.getInstance().getAllItems();

        allItems.forEach(obj->{
            supplierNameList.add(obj.getId());
        });

        cmbItemSupplierName.setItems(supplierNameList);
    }

    public void btnOnActionAdd(ActionEvent actionEvent) {

        if (
                service.addItem(
                        new Item(
                                txtId.getText(),
                                txtName.getText(),
                                txtSize.getText(),
                                Double.parseDouble(txtPrice.getText()),
                                Integer.parseInt(txtQuantity.getText()),
                                cmbItemCategory.getValue().toString(),
                                Integer.parseInt(txtStock.getText()),
                                cmbItemSupplierName.getValue().toString()
                        )
                )
        ) {
            new Alert(Alert.AlertType.INFORMATION, "Item Added!!").show();
            loadTable();

        } else {
            new Alert(Alert.AlertType.ERROR, "Item Not Added!!").show();

        }

    }

    public void btnOnActionUpdate(ActionEvent actionEvent) {
    }

    public void btnOnActionRemove(ActionEvent actionEvent) {
    }
}
