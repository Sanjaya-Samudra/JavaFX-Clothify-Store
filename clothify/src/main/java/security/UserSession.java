package security;

import model.User;
import java.time.LocalDateTime;

/**
 * Thread-safe singleton representing the currently logged in user session.
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private LocalDateTime loginTime;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public synchronized void login(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
    }

    public synchronized void logout() {
        this.currentUser = null;
        this.loginTime = null;
    }

    public synchronized void clearSession() {
        logout();
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized String getUserDisplayName() {
        if (currentUser == null) return "Cashier";
        if (currentUser.getName() != null && !currentUser.getName().trim().isEmpty()) {
            return currentUser.getName();
        }
        return currentUser.getEmail() != null ? currentUser.getEmail() : "Cashier";
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public synchronized boolean isCashier() {
        return currentUser != null && "CASHIER".equalsIgnoreCase(currentUser.getRole());
    }

    public synchronized boolean hasPermission(String moduleKey) {
        if (currentUser == null) return false;
        return currentUser.hasPermission(moduleKey);
    }

    public synchronized String getEffectivePermissions() {
        if (currentUser == null) return "ORDER,PRODUCT,REPORT";
        return currentUser.getEffectivePermissions();
    }

    public synchronized LocalDateTime getLoginTime() {
        return loginTime;
    }
}
