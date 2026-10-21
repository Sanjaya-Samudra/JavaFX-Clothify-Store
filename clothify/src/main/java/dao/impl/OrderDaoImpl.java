package dao.impl;

import controller.util.CrudUtil;
import dao.OrderDao;
import model.Order;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    @Override
    public boolean save(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_id, order_date, order_time, customer_name, customer_email, customer_phone, payment_type, sub_total, discount, total_cost, employee_id, employee_name, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                order.getOrderId(),
                Date.valueOf(order.getOrderDate()),
                Time.valueOf(order.getOrderTime()),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getPaymentType(),
                order.getSubTotal(),
                order.getDiscount(),
                order.getTotalCost(),
                order.getEmployeeId(),
                order.getEmployeeName(),
                order.getStatus() != null ? order.getStatus() : "COMPLETED"
        );
    }

    @Override
    public boolean update(Order order) throws SQLException {
        String sql = "UPDATE orders SET customer_name=?, customer_email=?, customer_phone=?, payment_type=?, sub_total=?, discount=?, total_cost=?, status=? WHERE order_id=?";
        return CrudUtil.execute(sql,
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getPaymentType(),
                order.getSubTotal(),
                order.getDiscount(),
                order.getTotalCost(),
                order.getStatus(),
                order.getOrderId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id=?";
        return CrudUtil.execute(sql, id);
    }

    @Override
    public Order search(String id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id=?";
        try (ResultSet rs = CrudUtil.execute(sql, id)) {
            if (rs.next()) {
                return extractOrder(rs);
            }
            return null;
        }
    }

    @Override
    public List<Order> getAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC, order_time DESC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        }
        return list;
    }

    @Override
    public List<Order> getRecentOrders(int limit) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC, order_time DESC LIMIT ?";
        try (ResultSet rs = CrudUtil.execute(sql, limit)) {
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        }
        return list;
    }

    @Override
    public Double getTodaySales(LocalDate date) throws SQLException {
        String sql = "SELECT SUM(total_cost) FROM orders WHERE order_date=? AND status != 'CANCELLED'";
        try (ResultSet rs = CrudUtil.execute(sql, Date.valueOf(date))) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        }
    }

    @Override
    public int getTodayOrdersCount(LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_date=? AND status != 'CANCELLED'";
        try (ResultSet rs = CrudUtil.execute(sql, Date.valueOf(date))) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    @Override
    public boolean updateStatus(String orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE order_id=?";
        return CrudUtil.execute(sql, status, orderId);
    }

    private Order extractOrder(ResultSet rs) throws SQLException {
        Date d = rs.getDate("order_date");
        Time t = rs.getTime("order_time");
        return new Order(
                rs.getString("order_id"),
                d != null ? d.toLocalDate() : LocalDate.now(),
                t != null ? t.toLocalTime() : null,
                rs.getString("customer_name"),
                rs.getString("customer_email"),
                rs.getString("customer_phone"),
                rs.getString("payment_type"),
                rs.getDouble("sub_total"),
                rs.getDouble("discount"),
                rs.getDouble("total_cost"),
                rs.getString("employee_id"),
                rs.getString("employee_name"),
                rs.getString("status")
        );
    }
}
