package controller.user;

import dao.UserDao;
import dao.impl.UserDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import security.PasswordSecurity;
import security.UserSession;

import java.sql.SQLException;
import java.util.List;

public class UserController implements UserService {

    private static UserController instance;
    private final UserDao userDao = new UserDaoImpl();

    private UserController() {}

    public static synchronized UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    @Override
    public boolean addUser(User user) {
        try {
            // Hash password securely if not already hashed
            String rawPass = user.getPassword();
            if (rawPass != null && !rawPass.contains("$")) {
                user.setPassword(PasswordSecurity.hashPassword(rawPass));
            }
            return userDao.save(user);
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyLogin(String email, String password) {
        try {
            User user = userDao.findByEmail(email);
            if (user != null && "ACTIVE".equalsIgnoreCase(user.getStatus())) {
                boolean matches = PasswordSecurity.verifyPassword(password, user.getPassword());
                if (matches) {
                    // Transparent security upgrade: If user had legacy plaintext password, migrate to salted hash
                    if (!user.getPassword().contains("$")) {
                        String newHashed = PasswordSecurity.hashPassword(password);
                        userDao.updatePassword(email, newHashed);
                        user.setPassword(newHashed);
                    }
                    // Register session
                    UserSession.getInstance().login(user);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login verification failed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updatePassword(String email, String newPassword) {
        try {
            String hashed = PasswordSecurity.hashPassword(newPassword);
            return userDao.updatePassword(email, hashed);
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ObservableList<User> getAllUsers() {
        ObservableList<User> list = FXCollections.observableArrayList();
        try {
            List<User> users = userDao.getAll();
            list.addAll(users);
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateUser(User user) {
        try {
            return userDao.update(user);
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(String id) {
        try {
            return userDao.delete(id);
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return userDao.findByEmail(email);
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return null;
        }
    }
}
