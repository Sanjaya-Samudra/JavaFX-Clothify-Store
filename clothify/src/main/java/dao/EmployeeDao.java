package dao;

import model.Employee;
import java.sql.SQLException;

public interface EmployeeDao extends CrudDao<Employee, String> {
    int getTotalEmployeeCount() throws SQLException;
}
