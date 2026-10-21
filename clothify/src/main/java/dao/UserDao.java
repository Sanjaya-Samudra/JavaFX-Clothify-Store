package dao;

import model.User;
import java.sql.SQLException;

public interface UserDao extends CrudDao<User, String> {
    User findByEmail(String email) throws SQLException;
    boolean updatePassword(String email, String newHashedPassword) throws SQLException;
    boolean updateRoleAndStatus(String userId, String role, String status) throws SQLException;
}
