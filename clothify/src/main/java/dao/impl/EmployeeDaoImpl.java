package dao.impl;

import controller.util.CrudUtil;
import dao.EmployeeDao;
import model.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public boolean save(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (employee_id, name, email, contact, company, role, salary) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getContact(),
                employee.getCompany(),
                employee.getRole(),
                employee.getSalary()
        );
    }

    @Override
    public boolean update(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET name=?, email=?, contact=?, company=?, role=?, salary=? WHERE employee_id=?";
        return CrudUtil.execute(sql,
                employee.getName(),
                employee.getEmail(),
                employee.getContact(),
                employee.getCompany(),
                employee.getRole(),
                employee.getSalary(),
                employee.getEmployeeId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM employee WHERE employee_id=?";
        return CrudUtil.execute(sql, id);
    }

    @Override
    public Employee search(String id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE employee_id=?";
        try (ResultSet rs = CrudUtil.execute(sql, id)) {
            if (rs.next()) {
                return extractEmployee(rs);
            }
            return null;
        }
    }

    @Override
    public List<Employee> getAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employee ORDER BY employee_id ASC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                list.add(extractEmployee(rs));
            }
        }
        return list;
    }

    @Override
    public int getTotalEmployeeCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM employee";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    private Employee extractEmployee(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Employee(
                rs.getString("employee_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("contact"),
                rs.getString("company"),
                rs.getString("role"),
                rs.getDouble("salary"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
