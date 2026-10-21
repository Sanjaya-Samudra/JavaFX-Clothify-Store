package controller.item;

import controller.supplier.SupplierController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import model.Item;
import model.Supplier;
import util.AlertUtil;
import util.IdGenerator;
import util.ValidationUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ItemManagementController implements Initializable {

    @FXML
    private TextField txtItemCode;

    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<String> cmbSize;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtQuantity;

    @FXML
    private ComboBox<String> cmbSupplier;

    @FXML
    private ImageView itemImageView;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbTableCategoryFilter;

    @FXML
    private TableView<Item> tblItems;

    @FXML
    private TableColumn<Item, String> colCode;

    @FXML
    private TableColumn<Item, String> colName;

    @FXML
    private TableColumn<Item, String> colCategory;

    @FXML
    private TableColumn<Item, String> colSize;

    @FXML
    private TableColumn<Item, Double> colPrice;

    @FXML
    private TableColumn<Item, Integer> colStock;

    @FXML
    private TableColumn<Item, String> colSupplier;

    private final ItemService itemService = ItemController.getInstance();
    private ObservableList<Item> masterItemList = FXCollections.observableArrayList();
    private byte[] currentImageBytes = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initDropdowns();
        loadTable();
        clearForm();

        tblItems.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    private void initColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        colCode.setStyle("-fx-alignment: CENTER;");
        colSize.setStyle("-fx-alignment: CENTER;");
        colStock.setStyle("-fx-alignment: CENTER;");
        colPrice.setStyle("-fx-alignment: CENTER-RIGHT;");

        colPrice.setCellFactory(col -> new TableCell<Item, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("%,.2f", price));
            }
        });

        colStock.setCellFactory(col -> new TableCell<Item, Integer>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Integer qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || qty == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(qty.toString());
                    if (qty <= 0) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else if (qty <= 5) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        Label itemPlaceholder = new Label("No matching catalog items found");
        itemPlaceholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        tblItems.setPlaceholder(itemPlaceholder);
    }

    private void initDropdowns() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Gents", "Ladies", "Kids", "Accessories", "Footwear", "Winter"
        );
        cmbCategory.setItems(categories);

        ObservableList<String> filterCategories = FXCollections.observableArrayList("All Categories");
        filterCategories.addAll(categories);
        cmbTableCategoryFilter.setItems(filterCategories);
        cmbTableCategoryFilter.setValue("All Categories");

        cmbSize.setItems(FXCollections.observableArrayList(
                "XS", "S", "M", "L", "XL", "XXL", "FREE", "28", "30", "32", "34", "36", "38", "40", "42"
        ));

        // Load Suppliers from supplier service
        ObservableList<String> supplierNames = FXCollections.observableArrayList();
        for (Supplier s : SupplierController.getInstance().getAllSuppliers()) {
            supplierNames.add(s.getName());
        }
        if (supplierNames.isEmpty()) {
            supplierNames.addAll("Apex Fabrics Ltd", "Elegance Fashion Co", "Urban Stitch Ltd", "Silk & Cotton Mills");
        }
        cmbSupplier.setItems(supplierNames);
    }

    public void loadTable() {
        masterItemList = itemService.getAllItems();
        tblItems.setItems(masterItemList);
    }

    private void populateForm(Item item) {
        txtItemCode.setText(item.getItemCode());
        txtName.setText(item.getName());
        cmbCategory.setValue(item.getCategory());
        cmbSize.setValue(item.getSize());
        txtPrice.setText(item.getPrice() != null ? String.valueOf(item.getPrice()) : "0");
        txtQuantity.setText(item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "0");
        cmbSupplier.setValue(item.getSupplier());

        currentImageBytes = item.getImage();
        if (currentImageBytes != null && currentImageBytes.length > 0) {
            try {
                itemImageView.setImage(new Image(new ByteArrayInputStream(currentImageBytes)));
            } catch (Exception e) {
                itemImageView.setImage(null);
            }
        } else {
            itemImageView.setImage(null);
        }
    }

    private void clearForm() {
        txtItemCode.setText(IdGenerator.generateItemId());
        txtName.clear();
        cmbCategory.setValue(null);
        cmbSize.setValue("M");
        txtPrice.clear();
        txtQuantity.clear();
        cmbSupplier.setValue(null);
        currentImageBytes = null;
        itemImageView.setImage(null);
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!validateInputs()) return;

        Item item = new Item(
                txtItemCode.getText().trim(),
                txtName.getText().trim(),
                cmbSize.getValue(),
                Double.parseDouble(txtPrice.getText().trim()),
                Integer.parseInt(txtQuantity.getText().trim()),
                cmbCategory.getValue(),
                cmbSupplier.getValue(),
                currentImageBytes
        );

        if (itemService.addItem(item)) {
            AlertUtil.showSuccess("Product Added", "Apparel " + item.getName() + " successfully registered!");
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Operation Failed", "Could not add product. Verify code is unique.");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputs()) return;

        Item item = new Item(
                txtItemCode.getText().trim(),
                txtName.getText().trim(),
                cmbSize.getValue(),
                Double.parseDouble(txtPrice.getText().trim()),
                Integer.parseInt(txtQuantity.getText().trim()),
                cmbCategory.getValue(),
                cmbSupplier.getValue(),
                currentImageBytes
        );

        if (itemService.updateItem(item)) {
            AlertUtil.showSuccess("Product Updated", "Item " + item.getItemCode() + " successfully updated!");
            loadTable();
            clearForm();
        } else {
            AlertUtil.showError("Update Failed", "Could not update item. Ensure item exists.");
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String code = txtItemCode.getText().trim();
        if (code.isEmpty()) {
            AlertUtil.showWarning("Missing Code", "Please select or specify a product code to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Delete Product", "Delete product " + code + "?", "This action cannot be undone.");
        if (confirm) {
            if (itemService.deleteItem(code)) {
                AlertUtil.showSuccess("Deleted", "Product removed successfully!");
                loadTable();
                clearForm();
            } else {
                AlertUtil.showError("Error", "Could not delete product. It may be referenced in existing orders.");
            }
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
        tblItems.getSelectionModel().clearSelection();
    }

    @FXML
    void btnRefreshTableOnAction(ActionEvent event) {
        loadTable();
    }

    @FXML
    void btnBrowseImageOnAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Product Image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                currentImageBytes = fis.readAllBytes();
                itemImageView.setImage(new Image(new ByteArrayInputStream(currentImageBytes)));
            } catch (IOException e) {
                AlertUtil.showError("Image Error", "Failed to load selected image: " + e.getMessage());
            }
        }
    }

    @FXML
    void btnClearImageOnAction(ActionEvent event) {
        currentImageBytes = null;
        itemImageView.setImage(null);
    }

    @FXML
    void onSearchKeyReleased(KeyEvent event) {
        String query = txtSearch.getText() != null ? txtSearch.getText().trim().toLowerCase() : "";
        if (query.isEmpty()) {
            tblItems.setItems(masterItemList);
        } else {
            ObservableList<Item> filtered = FXCollections.observableArrayList();
            for (Item item : masterItemList) {
                if (item.getItemCode().toLowerCase().contains(query) ||
                        item.getName().toLowerCase().contains(query) ||
                        (item.getSupplier() != null && item.getSupplier().toLowerCase().contains(query))) {
                    filtered.add(item);
                }
            }
            tblItems.setItems(filtered);
        }
    }

    @FXML
    void onTableCategoryFilterChanged(ActionEvent event) {
        String cat = cmbTableCategoryFilter.getValue();
        if (cat == null || "All Categories".equals(cat)) {
            tblItems.setItems(masterItemList);
        } else {
            ObservableList<Item> filtered = FXCollections.observableArrayList();
            for (Item item : masterItemList) {
                if (cat.equalsIgnoreCase(item.getCategory())) {
                    filtered.add(item);
                }
            }
            tblItems.setItems(filtered);
        }
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(txtItemCode.getText())) {
            AlertUtil.showError("Validation Error", "Product code is required.");
            return false;
        }
        if (!ValidationUtil.isNotEmpty(txtName.getText())) {
            AlertUtil.showError("Validation Error", "Product name cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isPositiveDouble(txtPrice.getText())) {
            AlertUtil.showError("Validation Error", "Price must be a valid positive number.");
            return false;
        }
        if (!ValidationUtil.isPositiveInt(txtQuantity.getText())) {
            AlertUtil.showError("Validation Error", "Stock quantity must be a valid non-negative integer.");
            return false;
        }
        return true;
    }
}
