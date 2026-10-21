package controller.employee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import model.Employee;
import util.AlertUtil;
import util.IdGenerator;
import util.ValidationUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeFormController implements Initializable {

    @FXML
    private TextField txtEmpId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtContact;

    @FXML
    private TextField txtCompany;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private TextField txtSalary;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Employee> tblEmployees;

    @FXML
    private TableColumn<Employee, String> colId;

    @FXML
    private TableColumn<Employee, String> colName;

    @FXML
    private TableColumn<Employee, String> colEmail;

    @FXML
    private TableColumn<Employee, String> colContact;

    @FXML
    private TableColumn<Employee, String> colCompany;

    @FXML
    private TableColumn<Employee, String> colRole;

    @FXML
    private TableColumn<Employee, Double> colSalary;

    private final EmployeeService employeeService = EmployeeController.getInstance();
    private ObservableList<Employee> masterEmployeeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initRoles();
        loadTable();
        clearForm();

        tblEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    private void initColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("company"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        colId.setStyle("-fx-alignment: CENTER;");

        colSalary.setCellFactory(col -> new TableCell<Employee, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Double salary, boolean empty) {
                super.updateItem(salary, empty);
                if (empty || salary == null) setText(null);
                else setText(String.format("LKR %,d", salary.longValue()));
            }
        });

        Label empPlaceholder = new Label("No staff employees found");
        empPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        tblEmployees.setPlaceholder(empPlaceholder);
    }

    private void initRoles() {
        cmbRole.setItems(FXCollections.observableArrayList(
                "Store General Manager",
                "Senior Cashier & Sales Executive",
                "Inventory Manager",
                "Sales Associate",
                "Assistant Cashier",
                "Security & Facility"
        ));
    }

    public void loadTable() {
        masterEmployeeList = employeeService.getAllEmployees();
        tblEmployees.setItems(masterEmployeeList);
    }

    private void populateForm(Employee emp) {
        txtEmpId.setText(emp.getEmployeeId());
        txtName.setText(emp.getName());
        txtEmail.setText(emp.getEmail());
        txtContact.setText(emp.getContact());
        txtCompany.setText(emp.getCompany());
        cmbRole.setValue(emp.getRole());
        txtSalary.setText(emp.getSalary() != null ? String.valueOf(emp.getSalary()) : "0");
    }

    private void clearForm() {
        txtEmpId.setText(IdGenerator.generateEmployeeId());
        txtName.clear();
        txtEmail.clear();
        txtContact.clear();
        txtCompany.setText("Clothify Store");
        cmbRole.setValue("Sales Associate");
        txtSalary.clear();
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!validateInputs()) return;

        Employee emp = new Employee(
                txtEmpId.getText().trim(),
                txtName.getText().trim(),
                txtEmail.getText().trim(),
                txtContact.getText().trim(),
                txtCompany.getText().trim(),
                cmbRole.getValue() != null ? cmbRole.getValue() : "Staff",
                Double.parseDouble(txtSalary.getText().trim())
        );

        if (employeeService.addEmployee(emp)) {
            AlertUtil.showSuccess("Staff Registered", "Employee " + emp.getName() + " added successfully!");
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Save Failed", "Could not register employee. ID might already exist.");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputs()) return;

        Employee emp = new Employee(
                txtEmpId.getText().trim(),
                txtName.getText().trim(),
                txtEmail.getText().trim(),
                txtContact.getText().trim(),
                txtCompany.getText().trim(),
                cmbRole.getValue() != null ? cmbRole.getValue() : "Staff",
                Double.parseDouble(txtSalary.getText().trim())
        );

        if (employeeService.updateEmployee(emp)) {
            AlertUtil.showSuccess("Employee Updated", "Employee " + emp.getEmployeeId() + " updated!");
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Update Failed", "Could not update employee.");
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtEmpId.getText().trim();
        if (id.isEmpty()) {
            AlertUtil.showWarning("Missing ID", "Please select an employee to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Delete Employee", "Remove employee " + id + "?", "This will remove the staff record from Clothify.");
        if (confirm) {
            if (employeeService.deleteEmployee(id)) {
                AlertUtil.showSuccess("Deleted", "Employee removed successfully.");
                loadTable();
                clearForm();
            } else {
                AlertUtil.showError("Error", "Could not delete employee.");
            }
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
        tblEmployees.getSelectionModel().clearSelection();
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadTable();
    }

    @FXML
    void onSearchKeyReleased(KeyEvent event) {
        String query = txtSearch.getText() != null ? txtSearch.getText().trim().toLowerCase() : "";
        if (query.isEmpty()) {
            tblEmployees.setItems(masterEmployeeList);
        } else {
            ObservableList<Employee> filtered = FXCollections.observableArrayList();
            for (Employee e : masterEmployeeList) {
                if (e.getEmployeeId().toLowerCase().contains(query) ||
                        e.getName().toLowerCase().contains(query) ||
                        e.getEmail().toLowerCase().contains(query) ||
                        e.getRole().toLowerCase().contains(query)) {
                    filtered.add(e);
                }
            }
            tblEmployees.setItems(filtered);
        }
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(txtEmpId.getText())) {
            AlertUtil.showError("Validation Error", "Employee ID is required.");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtName.getText())) {
            AlertUtil.showError("Validation Error", "Employee name cannot be empty.");
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
        if (!ValidationUtil.isPositiveDouble(txtSalary.getText())) {
            AlertUtil.showError("Validation Error", "Salary must be a valid positive number.");
            return false;
        }
        return true;
    }
}
