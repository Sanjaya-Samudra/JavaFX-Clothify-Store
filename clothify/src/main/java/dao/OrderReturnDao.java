package dao;

import model.OrderReturn;
import java.sql.SQLException;
import java.util.List;

public interface OrderReturnDao {
    boolean save(OrderReturn orderReturn) throws SQLException;
    List<OrderReturn> getAll() throws SQLException;
    List<OrderReturn> getByOrderId(String orderId) throws SQLException;
}
