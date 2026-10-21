package controller.item;

import dao.ItemDao;
import dao.impl.ItemDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;
import model.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public class ItemController implements ItemService {

    private static ItemController instance;
    private final ItemDao itemDao = new ItemDaoImpl();

    private ItemController() {}

    public static synchronized ItemController getInstance() {
        if (instance == null) {
            instance = new ItemController();
        }
        return instance;
    }

    @Override
    public boolean addItem(Item item) {
        try {
            return itemDao.save(item);
        } catch (SQLException e) {
            System.err.println("Error adding item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ObservableList<Item> getAllItems() {
        ObservableList<Item> list = FXCollections.observableArrayList();
        try {
            list.addAll(itemDao.getAll());
        } catch (SQLException e) {
            System.err.println("Error fetching items: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateItem(Item item) {
        try {
            return itemDao.update(item);
        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteItem(String itemCode) {
        try {
            return itemDao.delete(itemCode);
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Item searchItem(String id) {
        try {
            return itemDao.search(id);
        } catch (SQLException e) {
            System.err.println("Error searching item: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateStock(List<OrderDetail> orderDetails) {
        try {
            for (OrderDetail detail : orderDetails) {
                boolean ok = itemDao.decreaseQuantity(detail.getItemCode(), detail.getQty());
                if (!ok) return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ObservableList<Item> getLowStockItems(int threshold) {
        ObservableList<Item> list = FXCollections.observableArrayList();
        try {
            list.addAll(itemDao.getLowStockItems(threshold));
        } catch (SQLException e) {
            System.err.println("Error fetching low stock: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Item> getItemsByCategory(String category) {
        ObservableList<Item> list = FXCollections.observableArrayList();
        try {
            list.addAll(itemDao.getByCategory(category));
        } catch (SQLException e) {
            System.err.println("Error fetching category items: " + e.getMessage());
        }
        return list;
    }

    @Override
    public int getTotalProductCount() {
        try {
            return itemDao.getTotalProductCount();
        } catch (SQLException e) {
            return 0;
        }
    }
}
