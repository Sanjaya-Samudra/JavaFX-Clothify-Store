package controller.supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import model.Supplier;
import util.AlertUtil;
import util.IdGenerator;
import util.ValidationUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class SupplierFormController implements Initializable {

    @FXML
    private TextField txtSupplierId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtCompany;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtContact;

    @FXML
    private ComboBox<String> cmbItemCategory;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Supplier> tblSuppliers;

    @FXML
    private TableColumn<Supplier, String> colId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colCompany;

    @FXML
    private TableColumn<Supplier, String> colEmail;

    @FXML
    private TableColumn<Supplier, String> colContact;

    @FXML
    private TableColumn<Supplier, String> colCategory;

    private final SupplierService supplierService = SupplierController.getInstance();
    private ObservableList<Supplier> masterSupplierList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initCategoryDropdown();
        loadTable();
        clearForm();

        tblSuppliers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    private void initColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("company"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        colId.setStyle("-fx-alignment: CENTER;");

        Label supPlaceholder = new Label("No registered suppliers found");
        supPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        tblSuppliers.setPlaceholder(supPlaceholder);
    }

    private void initCategoryDropdown() {
        cmbItemCategory.setItems(FXCollections.observableArrayList(
                "Gents Clothing", "Ladies Wear", "Kids Wear & Footwear", "Accessories", "Fabrics & Textiles", "Winter Wear"
        ));
    }

    public void loadTable() {
        masterSupplierList = supplierService.getAllSuppliers();
        tblSuppliers.setItems(masterSupplierList);
    }

    private void populateForm(Supplier s) {
        txtSupplierId.setText(s.getSupplierId());
        txtName.setText(s.getName());
        txtCompany.setText(s.getCompany());
        txtEmail.setText(s.getEmail());
        txtContact.setText(s.getContact());
        cmbItemCategory.setValue(s.getItemCategory());
    }

    private void clearForm() {
        txtSupplierId.setText(IdGenerator.generateSupplierId());
        txtName.clear();
        txtCompany.clear();
        txtEmail.clear();
        txtContact.clear();
        cmbItemCategory.setValue(null);
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!validateInputs()) return;

        Supplier supplier = new Supplier(
                txtSupplierId.getText().trim(),
                txtName.getText().trim(),
                txtCompany.getText().trim(),
                txtEmail.getText().trim(),
                txtContact.getText().trim(),
                cmbItemCategory.getValue() != null ? cmbItemCategory.getValue() : "General Apparel"
        );

        if (supplierService.addSupplier(supplier)) {
            AlertUtil.showSuccess("Supplier Saved", "Supplier " + supplier.getName() + " registered successfully!");
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Save Failed", "Could not register supplier. Ensure ID is unique.");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputs()) return;

        Supplier supplier = new Supplier(
                txtSupplierId.getText().trim(),
                txtName.getText().trim(),
                txtCompany.getText().trim(),
                txtEmail.getText().trim(),
                txtContact.getText().trim(),
                cmbItemCategory.getValue() != null ? cmbItemCategory.getValue() : "General Apparel"
        );

        if (supplierService.updateSupplier(supplier)) {
            AlertUtil.showSuccess("Supplier Updated", "Supplier " + supplier.getSupplierId() + " updated!");
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Update Failed", "Could not update supplier.");
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtSupplierId.getText().trim();
        if (id.isEmpty()) {
            AlertUtil.showWarning("Missing ID", "Please select a supplier to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Delete Supplier", "Delete supplier " + id + "?", "This will remove the vendor from the system.");
        if (confirm) {
            if (supplierService.deleteSupplier(id)) {
                AlertUtil.showSuccess("Deleted", "Supplier removed successfully.");
                loadTable();
                clearForm();
            } else {
                AlertUtil.showError("Error", "Could not delete supplier.");
            }
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
        tblSuppliers.getSelectionModel().clearSelection();
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadTable();
    }

    @FXML
    void onSearchKeyReleased(KeyEvent event) {
        String query = txtSearch.getText() != null ? txtSearch.getText().trim().toLowerCase() : "";
        if (query.isEmpty()) {
            tblSuppliers.setItems(masterSupplierList);
        } else {
            ObservableList<Supplier> filtered = FXCollections.observableArrayList();
            for (Supplier s : masterSupplierList) {
                if (s.getSupplierId().toLowerCase().contains(query) ||
                        s.getName().toLowerCase().contains(query) ||
                        s.getCompany().toLowerCase().contains(query) ||
                        s.getEmail().toLowerCase().contains(query)) {
                    filtered.add(s);
                }
            }
            tblSuppliers.setItems(filtered);
        }
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(txtSupplierId.getText())) {
            AlertUtil.showError("Validation Error", "Supplier ID is required.");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtName.getText())) {
            AlertUtil.showError("Validation Error", "Supplier name cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtCompany.getText())) {
            AlertUtil.showError("Validation Error", "Company name is required.");
            return false;
        }
        if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
            AlertUtil.showError("Validation Error", "Please provide a valid email address.");
            return false;
        }
        if (!ValidationUtil.isValidPhone(txtContact.getText())) {
            AlertUtil.showError("Validation Error", "Please provide a valid phone number.");
            return false;
        }
        return true;
    }
}
