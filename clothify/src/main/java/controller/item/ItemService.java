package controller.item;

import javafx.collections.ObservableList;
import model.Item;

import java.util.List;

public interface ItemService {
    boolean addItem(Item item);
    ObservableList<Item> getAllItems();
    boolean updateItem(Item item);
    boolean deleteItem(String itemCode);
    Item searchItem(String id);
    //boolean updateStock(List<OrderDetail> orderDetails);
}
