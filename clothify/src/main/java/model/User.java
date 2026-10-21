package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String id;
    private String email;
    private String password;
    private String title;
    private String name;
    private LocalDate dob;
    private Double salary;
    private String address;
    private String city;
    private String province;
    private String role;     // ADMIN, CASHIER, DEFAULT_USER
    private String status;   // ACTIVE, INACTIVE
    private String permissions; // e.g. "ORDER,PRODUCT,REPORT"

    public User(String id, String email, String password, String title, String name, LocalDate dob, Double salary, String address, String city, String province) {
        this(id, email, password, title, name, dob, salary, address, city, province, "ADMIN", "ACTIVE", "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER");
    }

    public User(String id, String email, String password, String title, String name, LocalDate dob, Double salary, String address, String city, String province, String role, String status) {
        this(id, email, password, title, name, dob, salary, address, city, province, role, status,
                "ADMIN".equalsIgnoreCase(role) ? "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER" : "ORDER,PRODUCT,REPORT");
    }

    public String getEffectivePermissions() {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER";
        }
        if (permissions != null && !permissions.trim().isEmpty()) {
            return permissions.trim();
        }
        return "ORDER,PRODUCT,REPORT";
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isCashier() {
        return "CASHIER".equalsIgnoreCase(role);
    }

    public boolean hasPermission(String moduleKey) {
        if (isAdmin()) return true;
        if (moduleKey == null) return false;
        String perms = getEffectivePermissions().toUpperCase();
        return perms.contains(moduleKey.toUpperCase());
    }
}
