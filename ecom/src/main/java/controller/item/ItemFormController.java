package controller.item;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dto.Item;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static sun.net.www.MimeTable.loadTable;

public class ItemFormController implements Initializable {


    public JFXTextField txtId;
    public JFXTextField txtName;
    public JFXTextField txtPrice;
    public JFXComboBox cmbItemCategory;
    public JFXComboBox cmbItemSupplierName;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colPrice;
    public TableColumn colSize;
    public TableColumn colQuantity;
    public TableColumn colStock;
    public TableColumn colCategory;
    public TableColumn colSupplierName;
    public TableView tblItems;
    public JFXTextField txtSize;
    public JFXTextField txtQuantity;
    public JFXTextField txtStock;

    ItemService service = ItemController.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    private void addValueToText(Object newVal) {
        txtId.setText(newVal.getItemCode());
        txtName.setText(newVal.getDescription());
        txtSize.setText(newVal.getPackSize());
        txtPrice.setText(newVal.getUnitPrice().toString());
        txtQuantity.setText(newVal.getQty().toString());
        txtStock.setText(newVal.getQty().toString());
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
                                cmbItemCategory.get
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
