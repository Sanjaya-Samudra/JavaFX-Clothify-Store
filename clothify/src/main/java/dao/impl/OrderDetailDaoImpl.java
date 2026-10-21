package dao.impl;

import controller.util.CrudUtil;
import dao.OrderDetailDao;
import model.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailDao {

    @Override
    public boolean save(OrderDetail detail) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, item_code, item_name, unit_price, qty, total) VALUES (?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                detail.getOrderId(),
                detail.getItemCode(),
                detail.getItemName(),
                detail.getUnitPrice(),
                detail.getQty(),
                detail.getTotal()
        );
    }

    @Override
    public boolean saveAll(List<OrderDetail> details) throws SQLException {
        for (OrderDetail detail : details) {
            if (!save(detail)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<OrderDetail> getByOrderId(String orderId) throws SQLException {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id=?";
        try (ResultSet rs = CrudUtil.execute(sql, orderId)) {
            while (rs.next()) {
                list.add(new OrderDetail(
                        rs.getInt("detail_id"),
                        rs.getString("order_id"),
                        rs.getString("item_code"),
                        rs.getString("item_name"),
                        rs.getDouble("unit_price"),
                        rs.getInt("qty"),
                        rs.getDouble("total")
                ));
            }
        }
        return list;
    }
}
