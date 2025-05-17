package controller.item;

import controller.util.CrudUtil;
import javafx.collections.ObservableList;
import model.Item;

import java.sql.SQLException;

public class ItemController implements ItemService{

    private static ItemController instance;
    private ItemController(){}

    public static ItemController getInstance(){
        return instance==null?instance=new ItemController():instance;
    }

    @Override
    public boolean addItem(Item item) {
        String SQL = "INSERT INTO Item values(?,?,?,?,?,?,?)";
        try {
            Object execute = CrudUtil.execute(SQL,
                    item.getItemCode(),
                    item.getName(),
                    item.getSize(),
                    item.getPrice(),
                    item.getQuantity(),
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
        return null;
    }

    @Override
    public boolean updateItem(Item item) {
        return false;
    }

    @Override
    public boolean deleteItem(String id) {
        return false;
    }

    @Override
    public Item searchItem(String id) {
        return null;
    }
}
