package controller.user;

import javafx.collections.ObservableList;
import model.User;

public interface UserService {
    boolean addUser(User user);
    boolean verifyLogin(String email, String password);
    boolean updatePassword(String email, String newPassword);
    ObservableList<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(String id);
    User getUserByEmail(String email);
}
