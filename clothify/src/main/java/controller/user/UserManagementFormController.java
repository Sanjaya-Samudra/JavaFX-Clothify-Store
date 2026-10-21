package controller.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import model.User;
import security.PasswordSecurity;
import security.UserSession;
import util.AlertUtil;
import util.IdGenerator;
import util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserManagementFormController implements Initializable {

    @FXML
    private TextField txtUserId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPassword;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private ComboBox<String> cmbStatus;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtProvince;

    @FXML
    private CheckBox chkPermOrder;

    @FXML
    private CheckBox chkPermProduct;

    @FXML
    private CheckBox chkPermReport;

    @FXML
    private CheckBox chkPermEmployee;

    @FXML
    private CheckBox chkPermSupplier;

    @FXML
    private CheckBox chkPermUser;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<User> tblUsers;

    @FXML
    private TableColumn<User, String> colId;

    @FXML
    private TableColumn<User, String> colName;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, String> colStatus;

    @FXML
    private TableColumn<User, String> colPermissions;

    @FXML
    private TableColumn<User, String> colCity;

    private final UserService userService = UserController.getInstance();
    private ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private String currentExistingPasswordHash = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initDropdowns();
        loadTable();
        clearForm();

        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });

        if (cmbRole != null) {
            cmbRole.valueProperty().addListener((obs, oldVal, newVal) -> {
                if ("ADMIN".equalsIgnoreCase(newVal)) {
                    chkPermOrder.setSelected(true);
                    chkPermProduct.setSelected(true);
                    chkPermReport.setSelected(true);
                    chkPermEmployee.setSelected(true);
                    chkPermSupplier.setSelected(true);
                    chkPermUser.setSelected(true);
                }
            });
        }
    }

    private void initColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));

        colId.setStyle("-fx-alignment: CENTER;");
        colRole.setStyle("-fx-alignment: CENTER;");
        colStatus.setStyle("-fx-alignment: CENTER;");

        if (colPermissions != null) {
            colPermissions.setCellValueFactory(cellData -> new SimpleStringProperty(describePermissions(cellData.getValue())));
            colPermissions.setCellFactory(col -> new TableCell<User, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Label badge = new Label(item);
                        badge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-background-radius: 12; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold;");
                        if (item.contains("All") || item.contains("6")) {
                            badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-background-radius: 12; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold;");
                        }
                        setGraphic(badge);
                        setText(null);
                    }
                }
            });
        }

        colRole.setCellFactory(col -> new TableCell<User, String>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(role);
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        badge.getStyleClass().add("badge-info");
                    } else {
                        badge.getStyleClass().add("badge-success");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        colStatus.setCellFactory(col -> new TableCell<User, String>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    if ("ACTIVE".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-success");
                    } else {
                        badge.getStyleClass().add("badge-danger");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        Label userPlaceholder = new Label("No user accounts found");
        userPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        tblUsers.setPlaceholder(userPlaceholder);
    }

    private String describePermissions(User u) {
        if (u == null) return "None";
        if ("ADMIN".equalsIgnoreCase(u.getRole())) {
            return "All 6 Modules";
        }
        String perms = u.getEffectivePermissions();
        int count = 0;
        if (perms.contains("ORDER")) count++;
        if (perms.contains("PRODUCT")) count++;
        if (perms.contains("REPORT")) count++;
        if (perms.contains("EMPLOYEE")) count++;
        if (perms.contains("SUPPLIER")) count++;
        if (perms.contains("USER")) count++;
        return count + " Modules";
    }

    private void initDropdowns() {
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "CASHIER", "DEFAULT_USER"));
        cmbStatus.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
    }

    public void loadTable() {
        masterUserList = userService.getAllUsers();
        tblUsers.setItems(masterUserList);
    }

    private void populateForm(User u) {
        txtUserId.setText(u.getId());
        txtName.setText(u.getName());
        txtEmail.setText(u.getEmail());
        txtPassword.clear();
        currentExistingPasswordHash = u.getPassword();
        cmbRole.setValue(u.getRole());
        cmbStatus.setValue(u.getStatus());
        txtCity.setText(u.getCity());
        txtProvince.setText(u.getProvince());

        String perms = u.getEffectivePermissions();
        if (chkPermOrder != null) chkPermOrder.setSelected(perms.contains("ORDER"));
        if (chkPermProduct != null) chkPermProduct.setSelected(perms.contains("PRODUCT"));
        if (chkPermReport != null) chkPermReport.setSelected(perms.contains("REPORT"));
        if (chkPermEmployee != null) chkPermEmployee.setSelected(perms.contains("EMPLOYEE"));
        if (chkPermSupplier != null) chkPermSupplier.setSelected(perms.contains("SUPPLIER"));
        if (chkPermUser != null) chkPermUser.setSelected(perms.contains("USER"));
    }

    private void clearForm() {
        txtUserId.setText(IdGenerator.generateUserId());
        txtName.clear();
        txtEmail.clear();
        txtPassword.clear();
        currentExistingPasswordHash = null;
        cmbRole.setValue("CASHIER");
        cmbStatus.setValue("ACTIVE");
        txtCity.setText("Colombo");
        txtProvince.setText("Western");

        // Cashier default: Order, Product, Report
        if (chkPermOrder != null) chkPermOrder.setSelected(true);
        if (chkPermProduct != null) chkPermProduct.setSelected(true);
        if (chkPermReport != null) chkPermReport.setSelected(true);
        if (chkPermEmployee != null) chkPermEmployee.setSelected(false);
        if (chkPermSupplier != null) chkPermSupplier.setSelected(false);
        if (chkPermUser != null) chkPermUser.setSelected(false);
    }

    private String collectSelectedPermissions() {
        if ("ADMIN".equalsIgnoreCase(cmbRole.getValue())) {
            return "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER";
        }
        List<String> list = new ArrayList<>();
        if (chkPermOrder != null && chkPermOrder.isSelected()) list.add("ORDER");
        if (chkPermProduct != null && chkPermProduct.isSelected()) list.add("PRODUCT");
        if (chkPermReport != null && chkPermReport.isSelected()) list.add("REPORT");
        if (chkPermEmployee != null && chkPermEmployee.isSelected()) list.add("EMPLOYEE");
        if (chkPermSupplier != null && chkPermSupplier.isSelected()) list.add("SUPPLIER");
        if (chkPermUser != null && chkPermUser.isSelected()) list.add("USER");

        if (list.isEmpty()) {
            return "ORDER,PRODUCT,REPORT"; // ensure core 3
        }
        return String.join(",", list);
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!ValidationUtil.isNotEmpty(txtUserId.getText())) {
            AlertUtil.showError("Validation Error", "User ID is required.");
            return;
        }
        if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
            AlertUtil.showError("Validation Error", "Please provide a valid email.");
            return;
        }
        if (!ValidationUtil.isNotEmpty(txtPassword.getText())) {
            AlertUtil.showError("Validation Error", "Password cannot be empty for new account.");
            return;
        }

        String perms = collectSelectedPermissions();

        User u = new User(
                txtUserId.getText().trim(),
                txtEmail.getText().trim(),
                txtPassword.getText(),
                "Mr/Ms",
                txtName.getText().trim(),
                LocalDate.now().minusYears(25),
                50000.0,
                "Clothify Outlet",
                txtCity.getText().trim(),
                txtProvince.getText().trim(),
                cmbRole.getValue() != null ? cmbRole.getValue() : "CASHIER",
                cmbStatus.getValue() != null ? cmbStatus.getValue() : "ACTIVE",
                perms
        );

        if (userService.addUser(u)) {
            AlertUtil.showSuccess("User Registered", "Account " + u.getEmail() + " created successfully with permissions: " + perms);
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Save Failed", "Could not register user account. Email might already exist.");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!ValidationUtil.isNotEmpty(txtUserId.getText()) || !ValidationUtil.isValidEmail(txtEmail.getText())) {
            AlertUtil.showError("Validation Error", "Please verify User ID and email format.");
            return;
        }

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null && txtUserId.getText().trim().equalsIgnoreCase(currentUser.getId())) {
            if ("INACTIVE".equalsIgnoreCase(cmbStatus.getValue())) {
                AlertUtil.showError("Operation Forbidden", "You cannot deactivate your own active user account.");
                return;
            }
        }

        String pass = currentExistingPasswordHash;
        if (ValidationUtil.isNotEmpty(txtPassword.getText())) {
            pass = PasswordSecurity.hashPassword(txtPassword.getText());
        }

        String perms = collectSelectedPermissions();

        User u = new User(
                txtUserId.getText().trim(),
                txtEmail.getText().trim(),
                pass,
                "Mr/Ms",
                txtName.getText().trim(),
                LocalDate.now().minusYears(25),
                50000.0,
                "Clothify Outlet",
                txtCity.getText().trim(),
                txtProvince.getText().trim(),
                cmbRole.getValue() != null ? cmbRole.getValue() : "CASHIER",
                cmbStatus.getValue() != null ? cmbStatus.getValue() : "ACTIVE",
                perms
        );

        if (userService.updateUser(u)) {
            AlertUtil.showSuccess("User Updated", "Account " + u.getId() + " permissions updated to: " + perms);
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Update Failed", "Could not update user account.");
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtUserId.getText().trim();
        if (id.isEmpty()) {
            AlertUtil.showWarning("Missing ID", "Please select a user account to delete.");
            return;
        }

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null && id.equalsIgnoreCase(currentUser.getId())) {
            AlertUtil.showError("Operation Forbidden", "You cannot delete your own currently active user account.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Delete Account", "Delete user account " + id + "?", "This user will no longer be able to log in.");
        if (confirm) {
            if (userService.deleteUser(id)) {
                AlertUtil.showSuccess("Deleted", "User account removed.");
                loadTable();
                clearForm();
            } else {
                AlertUtil.showError("Error", "Could not delete user.");
            }
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
        tblUsers.getSelectionModel().clearSelection();
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadTable();
    }

    @FXML
    void onSearchKeyReleased(KeyEvent event) {
        String query = txtSearch.getText() != null ? txtSearch.getText().trim().toLowerCase() : "";
        if (query.isEmpty()) {
            tblUsers.setItems(masterUserList);
        } else {
            ObservableList<User> filtered = FXCollections.observableArrayList();
            for (User u : masterUserList) {
                if (u.getId().toLowerCase().contains(query) ||
                        u.getName().toLowerCase().contains(query) ||
                        u.getEmail().toLowerCase().contains(query) ||
                        u.getRole().toLowerCase().contains(query) ||
                        u.getEffectivePermissions().toLowerCase().contains(query)) {
                    filtered.add(u);
                }
            }
            tblUsers.setItems(filtered);
        }
    }
}
