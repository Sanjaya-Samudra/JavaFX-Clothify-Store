package controller.supplier;

import dao.SupplierDao;
import dao.impl.SupplierDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Supplier;

import java.sql.SQLException;

public class SupplierController implements SupplierService {

    private static SupplierController instance;
    private final SupplierDao supplierDao = new SupplierDaoImpl();

    private SupplierController() {}

    public static synchronized SupplierController getInstance() {
        if (instance == null) {
            instance = new SupplierController();
        }
        return instance;
    }

    @Override
    public boolean addSupplier(Supplier supplier) {
        try {
            return supplierDao.save(supplier);
        } catch (SQLException e) {
            System.err.println("Error saving supplier: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        try {
            return supplierDao.update(supplier);
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteSupplier(String supplierId) {
        try {
            return supplierDao.delete(supplierId);
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Supplier searchSupplier(String supplierId) {
        try {
            return supplierDao.search(supplierId);
        } catch (SQLException e) {
            System.err.println("Error searching supplier: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ObservableList<Supplier> getAllSuppliers() {
        ObservableList<Supplier> list = FXCollections.observableArrayList();
        try {
            list.addAll(supplierDao.getAll());
        } catch (SQLException e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
        }
        return list;
    }

    @Override
    public int getTotalSupplierCount() {
        try {
            return supplierDao.getTotalSupplierCount();
        } catch (SQLException e) {
            return 0;
        }
    }
}
