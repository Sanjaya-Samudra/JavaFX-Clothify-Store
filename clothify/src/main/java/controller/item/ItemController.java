package controller.item;

import controller.util.CrudUtil;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String SQL = "UPDATE Item SET name=?, size=?, price=?, quantity=?, category=?, supplier=?, image=?  WHERE item_code=?";

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            psTm.setObject(1, item.getName());
            psTm.setObject(2, item.getSize());
            psTm.setObject(3, item.getPrice());
            psTm.setObject(4, item.getQuantity());
            psTm.setObject(5, item.getCategory());
            psTm.setObject(6, item.getSupplier());
            psTm.setObject(7, item.getImage());
            psTm.setObject(8, item.getItemCode());
            return psTm.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteItem(String itemCode) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            return connection.createStatement().executeUpdate("DELETE FROM Item WHERE item_code ='" + itemCode + "'") > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item searchItem(String id) {
        String SQL = "SELECT * FROM Item WHERE item_code='" + id + "'";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()) {
                return new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getBytes(8)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
