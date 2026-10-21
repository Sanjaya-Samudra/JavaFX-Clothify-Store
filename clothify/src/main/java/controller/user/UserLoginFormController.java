package controller.user;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import security.UserSession;
import util.AlertUtil;
import util.IdGenerator;
import util.ValidationUtil;
import util.WindowUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class UserLoginFormController {

    // === Sign-In Pane Components ===
    @FXML
    private VBox paneSignIn;

    @FXML
    private Button btnRoleAdmin;

    @FXML
    private Button btnRoleCashier;

    @FXML
    private SVGPath svgRoleAdminIcon;

    @FXML
    private SVGPath svgRoleCashierIcon;

    @FXML
    private Label lblRoleDesc;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Label lblMessage;

    @FXML
    private Button btnLogin;

    // === Forgot Password Pane Components ===
    @FXML
    private VBox paneForgotPassword;

    @FXML
    private TextField txtResetEmail;

    @FXML
    private PasswordField txtResetNewPass;

    @FXML
    private PasswordField txtResetConfirmPass;

    @FXML
    private Label lblResetMessage;

    // === Register Staff Pane Components ===
    @FXML
    private VBox paneRegister;

    @FXML
    private TextField txtRegName;

    @FXML
    private TextField txtRegEmail;

    @FXML
    private ComboBox<String> cmbRegRole;

    @FXML
    private TextField txtRegCity;

    @FXML
    private PasswordField txtRegPass;

    @FXML
    private Label lblRegMessage;

    private String selectedRole = "ADMIN";
    private final UserService userService = UserController.getInstance();

    @FXML
    public void initialize() {
        if (lblMessage != null) lblMessage.setText("");
        if (lblResetMessage != null) lblResetMessage.setText("");
        if (lblRegMessage != null) lblRegMessage.setText("");

        if (cmbRegRole != null) {
            cmbRegRole.setItems(FXCollections.observableArrayList("CASHIER", "ADMIN"));
            cmbRegRole.setValue("CASHIER");
        }

        // Initialize default role UI
        updateRoleSelectorUI();
    }

    // === Role Selection Handlers ===

    @FXML
    void onRoleAdminSelected(ActionEvent event) {
        selectedRole = "ADMIN";
        updateRoleSelectorUI();
    }

    @FXML
    void onRoleCashierSelected(ActionEvent event) {
        selectedRole = "CASHIER";
        updateRoleSelectorUI();
    }

    private void updateRoleSelectorUI() {
        if (btnRoleAdmin == null || btnRoleCashier == null || lblRoleDesc == null) return;

        if ("ADMIN".equalsIgnoreCase(selectedRole)) {
            btnRoleAdmin.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #f59e0b; -fx-border-radius: 8; -fx-cursor: hand;");
            btnRoleCashier.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-cursor: hand;");
            if (svgRoleAdminIcon != null) {
                svgRoleAdminIcon.setFill(Color.web("#f59e0b"));
            }
            if (svgRoleCashierIcon != null) {
                svgRoleCashierIcon.setFill(Color.web("#64748b"));
            }
            lblRoleDesc.setText("Atelier Executive Access • Full ERP Control, Reports, Staff & User Hub");
            lblRoleDesc.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-padding: 4 10; -fx-background-radius: 6; -fx-border-color: #fde68a; -fx-border-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            btnRoleAdmin.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-cursor: hand;");
            btnRoleCashier.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #10b981; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-cursor: hand;");
            if (svgRoleAdminIcon != null) {
                svgRoleAdminIcon.setFill(Color.web("#64748b"));
            }
            if (svgRoleCashierIcon != null) {
                svgRoleCashierIcon.setFill(Color.web("#10b981"));
            }
            lblRoleDesc.setText("Frontline POS Access • Counter Sales, Orders & Catalog Restock");
            lblRoleDesc.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-padding: 4 10; -fx-background-radius: 6; -fx-border-color: #a7f3d0; -fx-border-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

    // === Quick Demo Sign-In Handlers ===

    @FXML
    void btnFillAdminOnAction(ActionEvent event) {
        selectedRole = "ADMIN";
        updateRoleSelectorUI();
        if (email != null) email.setText("one@gmail.com");
        if (password != null) password.setText("one");
        if (lblMessage != null) lblMessage.setText("");
    }

    @FXML
    void btnFillCashierOnAction(ActionEvent event) {
        selectedRole = "CASHIER";
        updateRoleSelectorUI();
        if (email != null) email.setText("two@gmail.com");
        if (password != null) password.setText("two");
        if (lblMessage != null) lblMessage.setText("");
    }

    @FXML
    void onPasswordAction(ActionEvent event) {
        btnLoginOnAction(event);
    }

    // === Main Login Action ===

    @FXML
    public void btnLoginOnAction(ActionEvent actionEvent) {
        String userEmail = email.getText() != null ? email.getText().trim() : "";
        String userPass = password.getText() != null ? password.getText() : "";

        if (userEmail.isEmpty() || userPass.isEmpty()) {
            if (lblMessage != null) {
                lblMessage.setStyle("-fx-text-fill: #dc2626;");
                lblMessage.setText("Please enter both email and password.");
            }
            return;
        }

        if (userService.verifyLogin(userEmail, userPass)) {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null && "CASHIER".equalsIgnoreCase(currentUser.getRole()) && "ADMIN".equalsIgnoreCase(selectedRole)) {
                // Cashier logging in with Admin selected: auto-align seamlessly
                selectedRole = "CASHIER";
                updateRoleSelectorUI();
            }

            try {
                Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Stage stage = new Stage();
                Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/dashboard_form.fxml"))));
                stage.setScene(scene);
                stage.setTitle("Clothify Store - Fashion POS & ERP Management System");

                WindowUtil.fitToWindow(stage, 1260, 720, 960, 560, true);
                stage.show();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (lblMessage != null) {
                    lblMessage.setStyle("-fx-text-fill: #dc2626;");
                    lblMessage.setText("Error loading dashboard: " + e.getMessage());
                }
            }
        } else {
            if (lblMessage != null) {
                lblMessage.setStyle("-fx-text-fill: #dc2626;");
                lblMessage.setText("Invalid credentials or deactivated account. Please verify your details.");
            } else {
                AlertUtil.showError("Login Failed", "Invalid credentials. Please verify your email and password.");
            }
        }
    }

    // === Inline View Switchers (No disruptive external dialogs) ===

    @FXML
    void onSwitchToForgotPassword(ActionEvent event) {
        if (email != null && !email.getText().trim().isEmpty() && txtResetEmail != null) {
            txtResetEmail.setText(email.getText().trim());
        }
        if (lblResetMessage != null) lblResetMessage.setText("");
        switchPane(paneForgotPassword);
    }

    @FXML
    void onSwitchToRegister(ActionEvent event) {
        if (lblRegMessage != null) lblRegMessage.setText("");
        switchPane(paneRegister);
    }

    @FXML
    void onSwitchToSignIn(ActionEvent event) {
        if (lblMessage != null) lblMessage.setText("");
        switchPane(paneSignIn);
    }

    private void switchPane(VBox targetPane) {
        VBox[] panes = {paneSignIn, paneForgotPassword, paneRegister};
        for (VBox p : panes) {
            if (p != null) {
                if (p == targetPane) {
                    p.setVisible(true);
                    p.setManaged(true);
                    FadeTransition ft = new FadeTransition(Duration.millis(200), p);
                    ft.setFromValue(0.0);
                    ft.setToValue(1.0);
                    ft.play();
                } else {
                    p.setVisible(false);
                    p.setManaged(false);
                }
            }
        }
    }

    // === Inline Password Reset Execution ===

    @FXML
    void btnResetPasswordOnAction(ActionEvent event) {
        String resetEmail = txtResetEmail != null && txtResetEmail.getText() != null ? txtResetEmail.getText().trim() : "";
        String p1 = txtResetNewPass != null && txtResetNewPass.getText() != null ? txtResetNewPass.getText() : "";
        String p2 = txtResetConfirmPass != null && txtResetConfirmPass.getText() != null ? txtResetConfirmPass.getText() : "";

        if (resetEmail.isEmpty()) {
            showResetFeedback("Please enter your registered email address.", true);
            return;
        }
        if (!ValidationUtil.isValidEmail(resetEmail)) {
            showResetFeedback("Please enter a valid email address.", true);
            return;
        }
        if (p1.isEmpty() || p2.isEmpty()) {
            showResetFeedback("Please enter and confirm your new password.", true);
            return;
        }
        if (!p1.equals(p2)) {
            showResetFeedback("Passwords do not match. Please re-enter.", true);
            return;
        }
        if (p1.length() < 3) {
            showResetFeedback("Password must contain at least 3 characters.", true);
            return;
        }

        User user = userService.getUserByEmail(resetEmail);
        if (user == null) {
            showResetFeedback("No staff account found for: " + resetEmail, true);
            return;
        }

        if (userService.updatePassword(resetEmail, p1)) {
            showResetFeedback("✓ Password updated successfully! Returning to sign in...", false);
            if (email != null) email.setText(resetEmail);
            if (password != null) password.setText(p1);
            if (txtResetNewPass != null) txtResetNewPass.clear();
            if (txtResetConfirmPass != null) txtResetConfirmPass.clear();

            // Auto-align role with reset user
            if ("CASHIER".equalsIgnoreCase(user.getRole())) {
                selectedRole = "CASHIER";
            } else {
                selectedRole = "ADMIN";
            }
            updateRoleSelectorUI();

            AlertUtil.showSuccess("Password Reset Complete", "Account password updated for " + user.getName() + ".\nYou can now sign in.");
            switchPane(paneSignIn);
        } else {
            showResetFeedback("Failed to update password. Please check database connection.", true);
        }
    }

    private void showResetFeedback(String msg, boolean isError) {
        if (lblResetMessage != null) {
            lblResetMessage.setStyle(isError ? "-fx-text-fill: #dc2626;" : "-fx-text-fill: #059669;");
            lblResetMessage.setText(msg);
        }
    }

    // === Inline Staff Registration Execution ===

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        String name = txtRegName != null && txtRegName.getText() != null ? txtRegName.getText().trim() : "";
        String regEmail = txtRegEmail != null && txtRegEmail.getText() != null ? txtRegEmail.getText().trim() : "";
        String pass = txtRegPass != null && txtRegPass.getText() != null ? txtRegPass.getText() : "";
        String city = txtRegCity != null && txtRegCity.getText() != null ? txtRegCity.getText().trim() : "Colombo";
        String role = cmbRegRole != null && cmbRegRole.getValue() != null ? cmbRegRole.getValue() : "CASHIER";

        if (name.isEmpty()) {
            showRegFeedback("Please enter staff member full name.", true);
            return;
        }
        if (!ValidationUtil.isValidEmail(regEmail)) {
            showRegFeedback("Please enter a valid email format.", true);
            return;
        }
        if (pass.length() < 3) {
            showRegFeedback("Password must contain at least 3 characters.", true);
            return;
        }

        if (userService.getUserByEmail(regEmail) != null) {
            showRegFeedback("An account with email " + regEmail + " already exists.", true);
            return;
        }

        String newId = IdGenerator.generateUserId();
        User newUser = new User(
                newId,
                regEmail,
                pass,
                "Mr/Ms",
                name,
                LocalDate.now().minusYears(22),
                45000.0,
                "Clothify Store",
                city.isEmpty() ? "Colombo" : city,
                "Western",
                role,
                "ACTIVE"
        );

        if (userService.addUser(newUser)) {
            showRegFeedback("✓ Staff registered! Returning to Sign In...", false);
            if (email != null) email.setText(regEmail);
            if (password != null) password.setText(pass);
            if (txtRegName != null) txtRegName.clear();
            if (txtRegEmail != null) txtRegEmail.clear();
            if (txtRegPass != null) txtRegPass.clear();

            if ("ADMIN".equalsIgnoreCase(role)) {
                selectedRole = "ADMIN";
            } else {
                selectedRole = "CASHIER";
            }
            updateRoleSelectorUI();

            AlertUtil.showSuccess("Registration Complete", "Account successfully registered for " + name + "!\nYou can now sign in.");
            switchPane(paneSignIn);
        } else {
            showRegFeedback("Could not register user. Please check database connectivity.", true);
        }
    }

    private void showRegFeedback(String msg, boolean isError) {
        if (lblRegMessage != null) {
            lblRegMessage.setStyle(isError ? "-fx-text-fill: #dc2626;" : "-fx-text-fill: #059669;");
            lblRegMessage.setText(msg);
        }
    }
}
