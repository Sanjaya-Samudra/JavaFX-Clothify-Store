package controller.item;

import controller.util.CrudUtil;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemController implements ItemService{

    private static ItemController instance;
    private ItemController(){}

    public static ItemController getInstance(){
        return instance==null?instance=new ItemController():instance;
    }

    @Override
    public boolean addItem(Item item) {
        System.out.println("ADD Button Clicked");
        String SQL = "INSERT INTO Item values(?,?,?,?,?,?,?,?)";
        try {
            Object execute = CrudUtil.execute(SQL,
                    item.getItemCode(),
                    item.getName(),
                    item.getSize(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getCategory(),
                    item.getSupplier(),
                    item.getImage()
            );
            System.out.println(execute);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ObservableList<Item> getAllItems() {
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM Item";

        try {
            ResultSet resultSet = CrudUtil.execute(SQL);

            while (resultSet.next()){
                itemObservableList.add(new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getBytes(8)
                ));
            }
            return itemObservableList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateItem(Item item) {
        return false;
    }

    @Override
    public boolean deleteItem(String itemCode) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            return connection.createStatement().executeUpdate("DELETE FROM Item WHERE ItemCode ='" + itemCode + "'") > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item searchItem(String id) {
        return null;
    }
}
