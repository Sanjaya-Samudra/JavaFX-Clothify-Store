package dao.impl;

import controller.util.CrudUtil;
import dao.SupplierDao;
import model.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoImpl implements SupplierDao {

    @Override
    public boolean save(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO supplier (supplier_id, name, company, email, contact, item_category) VALUES (?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getCompany(),
                supplier.getEmail(),
                supplier.getContact(),
                supplier.getItemCategory()
        );
    }

    @Override
    public boolean update(Supplier supplier) throws SQLException {
        String sql = "UPDATE supplier SET name=?, company=?, email=?, contact=?, item_category=? WHERE supplier_id=?";
        return CrudUtil.execute(sql,
                supplier.getName(),
                supplier.getCompany(),
                supplier.getEmail(),
                supplier.getContact(),
                supplier.getItemCategory(),
                supplier.getSupplierId()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM supplier WHERE supplier_id=?";
        return CrudUtil.execute(sql, id);
    }

    @Override
    public Supplier search(String id) throws SQLException {
        String sql = "SELECT * FROM supplier WHERE supplier_id=?";
        try (ResultSet rs = CrudUtil.execute(sql, id)) {
            if (rs.next()) {
                return extractSupplier(rs);
            }
            return null;
        }
    }

    @Override
    public List<Supplier> getAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY supplier_id ASC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                list.add(extractSupplier(rs));
            }
        }
        return list;
    }

    @Override
    public int getTotalSupplierCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM supplier";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    private Supplier extractSupplier(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Supplier(
                rs.getString("supplier_id"),
                rs.getString("name"),
                rs.getString("company"),
                rs.getString("email"),
                rs.getString("contact"),
                rs.getString("item_category"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
