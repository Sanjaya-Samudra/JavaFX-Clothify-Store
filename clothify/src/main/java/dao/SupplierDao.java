package dao;

import model.Supplier;
import java.sql.SQLException;

public interface SupplierDao extends CrudDao<Supplier, String> {
    int getTotalSupplierCount() throws SQLException;
}
