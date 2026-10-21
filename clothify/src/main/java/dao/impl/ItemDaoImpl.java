package dao.impl;

import controller.util.CrudUtil;
import dao.ItemDao;
import model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean save(Item item) throws SQLException {
        String sql = "INSERT INTO item (item_code, name, size, price, quantity, category, supplier, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                item.getItemCode(),
                item.getName(),
                item.getSize(),
                item.getPrice(),
                item.getQuantity(),
                item.getCategory(),
                item.getSupplier(),
                item.getImage()
        );
    }

    @Override
    public boolean update(Item item) throws SQLException {
        String sql = "UPDATE item SET name=?, size=?, price=?, quantity=?, category=?, supplier=?, image=? WHERE item_code=?";
        return CrudUtil.execute(sql,
                item.getName(),
                item.getSize(),
                item.getPrice(),
                item.getQuantity(),
                item.getCategory(),
                item.getSupplier(),
                item.getImage(),
                item.getItemCode()
        );
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM item WHERE item_code=?";
        return CrudUtil.execute(sql, id);
    }

    @Override
    public Item search(String id) throws SQLException {
        String sql = "SELECT * FROM item WHERE item_code=?";
        try (ResultSet rs = CrudUtil.execute(sql, id)) {
            if (rs.next()) {
                return extractItem(rs);
            }
            return null;
        }
    }

    @Override
    public List<Item> getAll() throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM item ORDER BY item_code ASC";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            while (rs.next()) {
                list.add(extractItem(rs));
            }
        }
        return list;
    }

    @Override
    public List<Item> getByCategory(String category) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE category=? ORDER BY item_code ASC";
        try (ResultSet rs = CrudUtil.execute(sql, category)) {
            while (rs.next()) {
                list.add(extractItem(rs));
            }
        }
        return list;
    }

    @Override
    public List<Item> getLowStockItems(int threshold) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE quantity <= ? ORDER BY quantity ASC";
        try (ResultSet rs = CrudUtil.execute(sql, threshold)) {
            while (rs.next()) {
                list.add(extractItem(rs));
            }
        }
        return list;
    }

    @Override
    public boolean decreaseQuantity(String itemCode, int qty) throws SQLException {
        String sql = "UPDATE item SET quantity = quantity - ? WHERE item_code = ? AND quantity >= ?";
        return CrudUtil.execute(sql, qty, itemCode, qty);
    }

    @Override
    public boolean increaseQuantity(String itemCode, int qty) throws SQLException {
        String sql = "UPDATE item SET quantity = quantity + ? WHERE item_code = ?";
        return CrudUtil.execute(sql, qty, itemCode);
    }

    @Override
    public int getTotalProductCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM item";
        try (ResultSet rs = CrudUtil.execute(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    private Item extractItem(ResultSet rs) throws SQLException {
        byte[] img = rs.getBytes("image");
        String code = rs.getString("item_code");
        if ((img == null || img.length == 0) && code != null) {
            img = loadImageFromDisk(code);
        }
        return new Item(
                code,
                rs.getString("name"),
                rs.getString("size"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getString("category"),
                rs.getString("supplier"),
                img
        );
    }

    private byte[] loadImageFromDisk(String itemCode) {
        if (itemCode == null) return null;
        String[] candidatePaths = {
                System.getProperty("user.dir") + java.io.File.separator + "data" + java.io.File.separator + "item_images",
                "data/item_images",
                "clothify/data/item_images"
        };
        String[] exts = {".png", ".jpg", ".jpeg"};
        for (String base : candidatePaths) {
            for (String ext : exts) {
                java.io.File file = new java.io.File(base, itemCode + ext);
                if (file.exists() && file.isFile() && file.length() > 0) {
                    try {
                        return java.nio.file.Files.readAllBytes(file.toPath());
                    } catch (Exception ignored) {}
                }
            }
        }
        return null;
    }
}
