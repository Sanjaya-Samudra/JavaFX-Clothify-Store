package controller.employee;

import dao.EmployeeDao;
import dao.impl.EmployeeDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;

import java.sql.SQLException;

public class EmployeeController implements EmployeeService {

    private static EmployeeController instance;
    private final EmployeeDao employeeDao = new EmployeeDaoImpl();

    private EmployeeController() {}

    public static synchronized EmployeeController getInstance() {
        if (instance == null) {
            instance = new EmployeeController();
        }
        return instance;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        try {
            return employeeDao.save(employee);
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        try {
            return employeeDao.update(employee);
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteEmployee(String employeeId) {
        try {
            return employeeDao.delete(employeeId);
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Employee searchEmployee(String employeeId) {
        try {
            return employeeDao.search(employeeId);
        } catch (SQLException e) {
            System.err.println("Error searching employee: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ObservableList<Employee> getAllEmployees() {
        ObservableList<Employee> list = FXCollections.observableArrayList();
        try {
            list.addAll(employeeDao.getAll());
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
        return list;
    }

    @Override
    public int getTotalEmployeeCount() {
        try {
            return employeeDao.getTotalEmployeeCount();
        } catch (SQLException e) {
            return 0;
        }
    }
}
