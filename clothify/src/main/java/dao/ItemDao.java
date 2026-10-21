package dao;

import model.Item;
import java.sql.SQLException;
import java.util.List;

public interface ItemDao extends CrudDao<Item, String> {
    List<Item> getByCategory(String category) throws SQLException;
    List<Item> getLowStockItems(int threshold) throws SQLException;
    boolean decreaseQuantity(String itemCode, int qty) throws SQLException;
    boolean increaseQuantity(String itemCode, int qty) throws SQLException;
    int getTotalProductCount() throws SQLException;
}
