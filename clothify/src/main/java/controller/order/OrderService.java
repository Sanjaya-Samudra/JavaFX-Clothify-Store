package controller.order;

import javafx.collections.ObservableList;
import model.Order;
import model.OrderDetail;
import model.OrderReturn;

import java.util.List;

public interface OrderService {
    boolean placeOrder(Order order);
    ObservableList<Order> getAllOrders();
    ObservableList<Order> getRecentOrders(int limit);
    Order getOrder(String orderId);
    List<OrderDetail> getOrderDetails(String orderId);
    boolean processReturn(OrderReturn orderReturn);
}
