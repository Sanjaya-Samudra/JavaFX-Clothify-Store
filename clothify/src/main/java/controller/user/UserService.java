package controller.user;

import model.User;

public interface UserService {

    boolean addUser(User user);

    boolean verifyLogin(String email,String password);
}
