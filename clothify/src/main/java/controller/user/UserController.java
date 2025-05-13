package controller.user;

import db.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController implements UserService{

    private static UserController instance;

    private UserController(){}

    public static UserController getInstance(){
        return instance==null?instance=new UserController():instance;
    }


    @Override
    public boolean addUser(User user) {
        String SQL = "INSERT INTO User values(?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            PreparedStatement psTm = connection.prepareStatement(SQL);
            psTm.setObject(1, user.getId());
            psTm.setObject(2, user.getEmail());
            psTm.setObject(3, user.getPassword());
            psTm.setObject(4, user.getTitle());
            psTm.setObject(5, user.getName());
            psTm.setObject(6, user.getDob());
            psTm.setObject(7, user.getSalary());
            psTm.setObject(8, user.getAddress());
            psTm.setObject(9, user.getCity());
            psTm.setObject(10, user.getProvince());

            return psTm.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyLogin(String email,String password) {
        String SQL = "SELECT * FROM user WHERE email = ? AND password = ?";
        System.out.println("verifying");
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            PreparedStatement psTm = connection.prepareStatement(SQL);

            psTm.setString(1, email);
            psTm.setString(2, password); // In production, hash the password before comparison

            ResultSet rs = psTm.executeQuery();
            return rs.next(); // returns true if a user is found


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
