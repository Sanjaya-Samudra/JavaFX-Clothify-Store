package controller.supplier;

import javafx.collections.ObservableList;
import model.Supplier;

public interface SupplierService {
    boolean addSupplier(Supplier supplier);
    boolean updateSupplier(Supplier supplier);
    boolean deleteSupplier(String supplierId);
    Supplier searchSupplier(String supplierId);
    ObservableList<Supplier> getAllSuppliers();
    int getTotalSupplierCount();
}
