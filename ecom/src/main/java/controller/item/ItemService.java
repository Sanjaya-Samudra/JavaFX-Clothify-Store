package controller.item;

import javafx.collections.ObservableList;
import dto.Item;
import dto.OrderDetail;

import java.util.List;

public interface ItemService {
    boolean addItem(Item item);
    ObservableList<Item> getAllItems();
    boolean updateItem(Item item);
    boolean deleteItem(String id);
    Item searchItem(String id);

    boolean updateStock(List<OrderDetail> orderDetails);
}