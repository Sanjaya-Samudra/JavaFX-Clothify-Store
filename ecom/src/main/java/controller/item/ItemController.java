package controller.item;

import dto.Item;
import dto.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.CrudUtil;

import java.sql.SQLException;
import java.util.List;

public class ItemController implements ItemService {
    private static ItemController instance;
    private ItemController(){}

    public static ItemController getInstance(){
        return instance == null ?  instance = new ItemController(): instance;
    }

    @Override
    public boolean addItem(Item item){
        String SQL = "INSERT INTO Item values(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Object execute = CrudUtil.execute(SQL,
                    item.getId(),
                    item.getName(),
                    item.getSize(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getCategory(),
                    item.getStock(),
                    item.getSupplierName()
            );
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Item> getAllItems() {
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
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

    @Override
    public boolean updateStock(List<OrderDetail> orderDetails) {
        return false;
    }
}
