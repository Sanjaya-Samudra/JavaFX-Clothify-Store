package controller.employee;

import javafx.collections.ObservableList;
import model.Employee;

public interface EmployeeService {
    boolean addEmployee(Employee employee);
    boolean updateEmployee(Employee employee);
    boolean deleteEmployee(String employeeId);
    Employee searchEmployee(String employeeId);
    ObservableList<Employee> getAllEmployees();
    int getTotalEmployeeCount();
}
