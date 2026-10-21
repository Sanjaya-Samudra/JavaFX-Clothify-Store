package dao.impl;

import controller.util.CrudUtil;
import dao.UserDao;
import model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean save(User user) throws SQLException {
        String sql = "INSERT INTO user (id, email, password, title, name, dob, salary, address, city, province, role, status, permissions) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Date dobSql = user.getDob() != null ? Date.valueOf(user.getDob()) : null;
        return CrudUtil.execute(sql,
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getTitle(),
                user.getName(),
                dobSql,
                user.getSalary(),
                user.getAddress(),
                user.getCity(),
                user.getProvince(),
                user.getRole() != null ? user.getRole() : "ADMIN",
                user.getStatus() != null ? user.getStatus() : "ACTIVE",
                user.getEffectivePermissions()
        );
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE user SET email=?, title=?, name=?, dob=?, salary=?, address=?, city=?, province=?, role=?, status=?, permissions=? WHERE id=?";
        Date dobSql = user.getDob() != null ? Date.valueOf(user.getDob()) : null;
        return CrudUtil.execute(sql,
                user.getEmail(),
                user.getTitle(),
                user.getName(),
                dobSql,
                user.getSalary(),
                user.getAddress(),
                user.getCity(),
                user.getProvince(),
                user.getRole(),
                user.getStatus(),
                user.getEffectivePermissions(),
                user.getId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM user WHERE id=?";
        return CrudUtil.execute(sql, id);
    }

    @Override
    public User search(String id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id=?";
        try (ResultSet rs = CrudUtil.execute(sql, id)) {
            if (rs.next()) {
                return extractUser(rs);
            }
            return null;
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id ASC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                list.add(extractUser(rs));
            }
        }
        return list;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email=?";
        try (ResultSet rs = CrudUtil.execute(sql, email)) {
            if (rs.next()) {
                return extractUser(rs);
            }
            return null;
        }
    }

    @Override
    public boolean updatePassword(String email, String newHashedPassword) throws SQLException {
        String sql = "UPDATE user SET password=? WHERE email=?";
        return CrudUtil.execute(sql, newHashedPassword, email);
    }

    @Override
    public boolean updateRoleAndStatus(String userId, String role, String status) throws SQLException {
        String sql = "UPDATE user SET role=?, status=? WHERE id=?";
        return CrudUtil.execute(sql, role, status, userId);
    }

    private User extractUser(ResultSet rs) throws SQLException {
        Date dobDate = rs.getDate("dob");
        String role = "ADMIN";
        String status = "ACTIVE";
        try {
            role = rs.getString("role");
            if (role == null) role = "ADMIN";
        } catch (SQLException ignored) {}
        try {
            status = rs.getString("status");
            if (status == null) status = "ACTIVE";
        } catch (SQLException ignored) {}
        String permissions = "ORDER,PRODUCT,REPORT";
        try {
            permissions = rs.getString("permissions");
            if (permissions == null || permissions.trim().isEmpty()) {
                permissions = "ADMIN".equalsIgnoreCase(role) ? "ORDER,PRODUCT,REPORT,EMPLOYEE,SUPPLIER,USER" : "ORDER,PRODUCT,REPORT";
            }
        } catch (SQLException ignored) {}

        return new User(
                rs.getString("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("title"),
                rs.getString("name"),
                dobDate != null ? dobDate.toLocalDate() : null,
                rs.getDouble("salary"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("province"),
                role,
                status,
                permissions
        );
    }
}
