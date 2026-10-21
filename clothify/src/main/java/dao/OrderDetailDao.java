package dao;

import model.OrderDetail;
import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDao {
    boolean save(OrderDetail detail) throws SQLException;
    boolean saveAll(List<OrderDetail> details) throws SQLException;
    List<OrderDetail> getByOrderId(String orderId) throws SQLException;
}
