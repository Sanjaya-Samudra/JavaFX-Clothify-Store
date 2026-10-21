package dao.impl;

import controller.util.CrudUtil;
import dao.OrderReturnDao;
import model.OrderReturn;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderReturnDaoImpl implements OrderReturnDao {

    @Override
    public boolean save(OrderReturn orderReturn) throws SQLException {
        String sql = "INSERT INTO order_returns (return_id, order_id, item_code, return_qty, reason, refund_amount, return_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                orderReturn.getReturnId(),
                orderReturn.getOrderId(),
                orderReturn.getItemCode(),
                orderReturn.getReturnQty(),
                orderReturn.getReason(),
                orderReturn.getRefundAmount(),
                Date.valueOf(orderReturn.getReturnDate())
        );
    }

    @Override
    public List<OrderReturn> getAll() throws SQLException {
        List<OrderReturn> list = new ArrayList<>();
        String sql = "SELECT * FROM order_returns ORDER BY return_date DESC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                Date d = rs.getDate("return_date");
                list.add(new OrderReturn(
                        rs.getString("return_id"),
                        rs.getString("order_id"),
                        rs.getString("item_code"),
                        rs.getInt("return_qty"),
                        rs.getString("reason"),
                        rs.getDouble("refund_amount"),
                        d != null ? d.toLocalDate() : null
                ));
            }
        }
        return list;
    }

    @Override
    public List<OrderReturn> getByOrderId(String orderId) throws SQLException {
        List<OrderReturn> list = new ArrayList<>();
        String sql = "SELECT * FROM order_returns WHERE order_id=?";
        try (ResultSet rs = CrudUtil.execute(sql, orderId)) {
            while (rs.next()) {
                Date d = rs.getDate("return_date");
                list.add(new OrderReturn(
                        rs.getString("return_id"),
                        rs.getString("order_id"),
                        rs.getString("item_code"),
                        rs.getInt("return_qty"),
                        rs.getString("reason"),
                        rs.getDouble("refund_amount"),
                        d != null ? d.toLocalDate() : null
                ));
            }
        }
        return list;
    }
}
