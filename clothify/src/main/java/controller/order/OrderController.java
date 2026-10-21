package controller.order;

import dao.ItemDao;
import dao.OrderDao;
import dao.OrderDetailDao;
import dao.OrderReturnDao;
import dao.impl.ItemDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.OrderDetailDaoImpl;
import dao.impl.OrderReturnDaoImpl;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Order;
import model.OrderDetail;
import model.OrderReturn;
import util.ReceiptGenerator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderController implements OrderService {

    private static OrderController instance;

    private final OrderDao orderDao = new OrderDaoImpl();
    private final OrderDetailDao orderDetailDao = new OrderDetailDaoImpl();
    private final ItemDao itemDao = new ItemDaoImpl();
    private final OrderReturnDao orderReturnDao = new OrderReturnDaoImpl();

    private OrderController() {}

    public static synchronized OrderController getInstance() {
        if (instance == null) {
            instance = new OrderController();
        }
        return instance;
    }

    @Override
    public boolean placeOrder(Order order) {
        DBConnection db = null;
        try {
            db = DBConnection.getInstance();
            db.startTransaction();

            // 1. Save main order
            boolean orderSaved = orderDao.save(order);
            if (!orderSaved) {
                db.rollback();
                return false;
            }

            // 2. Save order details and deduct stock
            for (OrderDetail detail : order.getOrderDetails()) {
                detail.setOrderId(order.getOrderId());
                boolean detailSaved = orderDetailDao.save(detail);
                if (!detailSaved) {
                    db.rollback();
                    return false;
                }

                boolean stockDeducted = itemDao.decreaseQuantity(detail.getItemCode(), detail.getQty());
                if (!stockDeducted) {
                    db.rollback();
                    System.err.println("Insufficient stock for item: " + detail.getItemCode());
                    return false;
                }
            }

            // Commit atomic transaction
            db.commit();

            // Asynchronously or locally save receipt to disk
            try {
                ReceiptGenerator.saveReceiptToFile(order);
            } catch (Exception e) {
                System.err.println("Receipt save error: " + e.getMessage());
            }

            return true;
        } catch (SQLException e) {
            if (db != null) {
                db.rollback();
            }
            System.err.println("Transaction failed while placing order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ObservableList<Order> getAllOrders() {
        ObservableList<Order> list = FXCollections.observableArrayList();
        try {
            list.addAll(orderDao.getAll());
        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Order> getRecentOrders(int limit) {
        ObservableList<Order> list = FXCollections.observableArrayList();
        try {
            list.addAll(orderDao.getRecentOrders(limit));
        } catch (SQLException e) {
            System.err.println("Error fetching recent orders: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Order getOrder(String orderId) {
        try {
            Order order = orderDao.search(orderId);
            if (order != null) {
                order.setOrderDetails(orderDetailDao.getByOrderId(orderId));
            }
            return order;
        } catch (SQLException e) {
            System.err.println("Error retrieving order: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<OrderDetail> getOrderDetails(String orderId) {
        try {
            return orderDetailDao.getByOrderId(orderId);
        } catch (SQLException e) {
            System.err.println("Error retrieving order details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean processReturn(OrderReturn orderReturn) {
        DBConnection db = null;
        try {
            db = DBConnection.getInstance();
            db.startTransaction();

            // 1. Save return record
            boolean returnSaved = orderReturnDao.save(orderReturn);
            if (!returnSaved) {
                db.rollback();
                return false;
            }

            // 2. Restore item inventory stock
            boolean stockRestored = itemDao.increaseQuantity(orderReturn.getItemCode(), orderReturn.getReturnQty());
            if (!stockRestored) {
                db.rollback();
                return false;
            }

            // 3. Update order status to indicate returns
            orderDao.updateStatus(orderReturn.getOrderId(), "RETURNED");

            db.commit();
            return true;
        } catch (SQLException e) {
            if (db != null) {
                db.rollback();
            }
            System.err.println("Failed to process return: " + e.getMessage());
            return false;
        }
    }
}
