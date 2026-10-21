package dao;

import model.Order;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface OrderDao extends CrudDao<Order, String> {
    List<Order> getRecentOrders(int limit) throws SQLException;
    Double getTodaySales(LocalDate date) throws SQLException;
    int getTodayOrdersCount(LocalDate date) throws SQLException;
    boolean updateStatus(String orderId, String status) throws SQLException;
}
