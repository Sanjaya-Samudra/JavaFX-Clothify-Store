package controller.item;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Item;
import util.AlertUtil;
import util.ValidationUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ItemFormController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbCategory;

    @FXML
    private TableColumn<Item, String> colCategory;

    @FXML
    private TableColumn<Item, byte[]> colImage;

    @FXML
    private TableColumn<Item, String> colItemCode;

    @FXML
    private TableColumn<Item, String> colName;

    @FXML
    private TableColumn<Item, Double> colPrice;

    @FXML
    private TableColumn<Item, Integer> colQuantity;

    @FXML
    private TableColumn<Item, String> colSize;

    @FXML
    private TableColumn<Item, String> colSupplier;

    @FXML
    private TextField imageName;

    @FXML
    private ImageView itemImage;

    @FXML
    private VBox placeholderBox;

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private JFXTextField txtQuantity;

    @FXML
    private JFXTextField txtSize;

    @FXML
    private JFXTextField txtSupplier;

    @FXML
    private TableView<Item> tblItems;

    private byte[] loadedImageBytes = null;
    private final ItemService service = ItemController.getInstance();

    private static final String STORAGE_DIR_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "item_images";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(col -> new TableCell<Item, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("LKR %,.2f", price));
            }
        });
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Refined Haute Couture thumbnail rendering in table
        colImage.setCellFactory(col -> new TableCell<Item, byte[]>() {
            private final ImageView thumbnailView = new ImageView();
            private final StackPane imgContainer = new StackPane();
            private final Label placeholder = new Label("No Photo");

            {
                thumbnailView.setFitWidth(32);
                thumbnailView.setFitHeight(32);
                thumbnailView.setPreserveRatio(true);
                Circle clip = new Circle(16, 16, 16);
                thumbnailView.setClip(clip);

                imgContainer.setAlignment(Pos.CENTER);
                imgContainer.setPrefSize(36, 36);
                imgContainer.setMaxSize(36, 36);
                imgContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 18; -fx-border-color: #cbd5e1; -fx-border-radius: 18; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 1);");
                imgContainer.getChildren().add(thumbnailView);

                placeholder.setStyle("-fx-font-size: 9.5px; -fx-text-fill: #94a3b8; -fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 2 6 2 6; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-font-weight: bold;");
                placeholder.setMinWidth(Region.USE_PREF_SIZE);
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(byte[] bytes, boolean empty) {
                super.updateItem(bytes, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Item currentItem = getTableRow() != null ? getTableRow().getItem() : null;
                if (currentItem == null && bytes == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                byte[] imgData = bytes;
                if (imgData == null && currentItem != null) {
                    imgData = currentItem.getImage();
                }
                if ((imgData == null || imgData.length == 0) && currentItem != null && currentItem.getItemCode() != null) {
                    imgData = loadImageFromDisk(currentItem.getItemCode());
                    if (imgData != null && currentItem.getImage() == null) {
                        currentItem.setImage(imgData);
                    }
                }

                if (imgData != null && imgData.length > 0) {
                    try {
                        thumbnailView.setImage(new Image(new ByteArrayInputStream(imgData)));
                        setGraphic(imgContainer);
                        setText(null);
                    } catch (Exception e) {
                        setGraphic(placeholder);
                        setText(null);
                    }
                } else {
                    setGraphic(placeholder);
                    setText(null);
                }
            }
        });

        ObservableList<String> titleList = FXCollections.observableArrayList();
        titleList.addAll("Gents", "Ladies", "Kids", "Accessories", "Footwear", "Winter");
        cmbCategory.setItems(titleList);

        tblItems.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null) {
                addValueToText(newVal);
            }
        });

        // Soft rounded clip for main enlarged preview image
        Rectangle previewClip = new Rectangle(280, 175);
        previewClip.setArcWidth(14);
        previewClip.setArcHeight(14);
        itemImage.setClip(previewClip);

        loadTable();
        if (!tblItems.getItems().isEmpty()) {
            tblItems.getSelectionModel().select(0);
        } else {
            updateImagePreview(null, null, "");
        }

        // Dynamic Responsive Placeholders: detailed when maximized/wide, compact when restored/minimized
        Platform.runLater(() -> {
            if (tblItems != null && tblItems.getScene() != null && tblItems.getScene().getWindow() != null) {
                Stage stage = (Stage) tblItems.getScene().getWindow();
                stage.maximizedProperty().addListener((obs, wasMax, isMax) -> updatePlaceholders(isMax));
                stage.widthProperty().addListener((obs, oldW, newW) -> {
                    boolean isWide = newW.doubleValue() > 1150.0 || stage.isMaximized();
                    updatePlaceholders(isWide);
                });
                updatePlaceholders(stage.isMaximized() || stage.getWidth() > 1150.0);
            }
        });
    }

    /**
     * Dynamically adjusts form and table placeholders based on window state.
     * When maximized/enlarged: adds descriptive, detailed placeholders.
     * When minimized/restored: reduces placeholders to clean, compact prompts to prevent clutter.
     */
    public void updatePlaceholders(boolean isMaximized) {
        if (isMaximized) {
            if (txtItemCode != null) txtItemCode.setPromptText("Item Code (e.g. P001 - Unique SKU)");
            if (cmbCategory != null) cmbCategory.setPromptText("Select Category");
            if (txtName != null) txtName.setPromptText("Apparel / Product Name (e.g. Slim-Fit Cotton Oxford Shirt)");
            if (txtSize != null) txtSize.setPromptText("Size (S, M, L, XL, 32)");
            if (txtPrice != null) txtPrice.setPromptText("Unit Selling Price in LKR (e.g. 4500.00)");
            if (txtQuantity != null) txtQuantity.setPromptText("Stock Quantity in Units (e.g. 50)");
            if (txtSupplier != null) txtSupplier.setPromptText("Supplier Name or Vendor ID (e.g. Apex Fab / S001)");
            if (imageName != null && (imageName.getText() == null || imageName.getText().isEmpty() || imageName.getText().startsWith("No image"))) {
                imageName.setPromptText("Apparel photo file path (Click Browse to upload)...");
            }
            if (tblItems != null) {
                Label placeholder = new Label("✨ No apparel items found in catalog. Use the form above to add a product.");
                placeholder.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-font-weight: bold;");
                tblItems.setPlaceholder(placeholder);
            }
        } else {
            if (txtItemCode != null) txtItemCode.setPromptText("Item Code (P001)");
            if (cmbCategory != null) cmbCategory.setPromptText("Category");
            if (txtName != null) txtName.setPromptText("Apparel Name");
            if (txtSize != null) txtSize.setPromptText("Size");
            if (txtPrice != null) txtPrice.setPromptText("Price (LKR)");
            if (txtQuantity != null) txtQuantity.setPromptText("Qty");
            if (txtSupplier != null) txtSupplier.setPromptText("Supplier");
            if (imageName != null && (imageName.getText() == null || imageName.getText().isEmpty() || imageName.getText().startsWith("No image"))) {
                imageName.setPromptText("No image chosen");
            }
            if (tblItems != null) {
                Label placeholder = new Label("No items in catalog");
                placeholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
                tblItems.setPlaceholder(placeholder);
            }
        }
    }

    private void updateImagePreview(Image image, byte[] bytes, String caption) {
        if (image != null) {
            if (placeholderBox != null) {
                placeholderBox.setVisible(false);
                placeholderBox.setManaged(false);
            }
            itemImage.setImage(image);
            itemImage.setVisible(true);
            itemImage.setManaged(true);
            if (imageName != null) {
                int kb = bytes != null ? (bytes.length / 1024) : 0;
                imageName.setText(caption != null && !caption.isEmpty() ? caption : (kb > 0 ? "Photo Attached (" + kb + " KB)" : "Photo Attached"));
            }
        } else {
            itemImage.setImage(null);
            itemImage.setVisible(false);
            itemImage.setManaged(false);
            if (placeholderBox != null) {
                placeholderBox.setVisible(true);
                placeholderBox.setManaged(true);
            }
            if (imageName != null) {
                imageName.setText(caption != null ? caption : "");
            }
        }
    }

    private void addValueToText(Item newVal) {
        if (newVal == null) return;
        txtItemCode.setText(newVal.getItemCode() != null ? newVal.getItemCode() : "");
        txtName.setText(newVal.getName() != null ? newVal.getName() : "");
        txtQuantity.setText(newVal.getQuantity() != null ? newVal.getQuantity().toString() : "0");
        txtPrice.setText(newVal.getPrice() != null ? newVal.getPrice().toString() : "0");
        txtSize.setText(newVal.getSize() != null ? newVal.getSize() : "");
        cmbCategory.setValue(newVal.getCategory());
        txtSupplier.setText(newVal.getSupplier() != null ? newVal.getSupplier() : "");

        loadedImageBytes = newVal.getImage();
        if ((loadedImageBytes == null || loadedImageBytes.length == 0) && newVal.getItemCode() != null) {
            loadedImageBytes = loadImageFromDisk(newVal.getItemCode());
            if (loadedImageBytes != null) {
                newVal.setImage(loadedImageBytes);
            }
        }

        if (loadedImageBytes != null && loadedImageBytes.length > 0) {
            try {
                InputStream is = new ByteArrayInputStream(loadedImageBytes);
                Image image = new Image(is);
                updateImagePreview(image, loadedImageBytes, "Photo Attached (" + (loadedImageBytes.length / 1024) + " KB)");
            } catch (Exception e) {
                updateImagePreview(null, null, "Failed to render image");
            }
        } else {
            updateImagePreview(null, null, "");
        }
    }

    private void loadTable() {
        tblItems.setItems(service.getAllItems());
    }

    private void selectItemInTable(String code) {
        if (code == null) return;
        for (Item it : tblItems.getItems()) {
            if (code.equalsIgnoreCase(it.getItemCode())) {
                tblItems.getSelectionModel().select(it);
                tblItems.scrollTo(it);
                break;
            }
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        try {
            String code = txtItemCode.getText() != null ? txtItemCode.getText().trim() : "";
            if (code.isEmpty()) {
                AlertUtil.showError("Validation Error", "Product Code cannot be empty.");
                return;
            }
            if (!ValidationUtil.isNotEmpty(txtName.getText())) {
                AlertUtil.showError("Validation Error", "Product Name cannot be empty.");
                return;
            }
            if (!ValidationUtil.isPositiveDouble(txtPrice.getText())) {
                AlertUtil.showError("Validation Error", "Price must be a valid positive number.");
                return;
            }
            if (!ValidationUtil.isPositiveInt(txtQuantity.getText())) {
                AlertUtil.showError("Validation Error", "Quantity must be a valid non-negative integer.");
                return;
            }

            Item item = new Item(
                    code,
                    txtName.getText().trim(),
                    txtSize.getText() != null ? txtSize.getText().trim() : "",
                    Double.parseDouble(txtPrice.getText().trim()),
                    Integer.parseInt(txtQuantity.getText().trim()),
                    cmbCategory.getValue() != null ? cmbCategory.getValue() : "Gents",
                    txtSupplier.getText() != null ? txtSupplier.getText().trim() : "",
                    loadedImageBytes
            );

            if (service.addItem(item)) {
                if (loadedImageBytes != null && loadedImageBytes.length > 0) {
                    saveImageToDisk(code, loadedImageBytes);
                }
                AlertUtil.showSuccess("Item Added", "Apparel product '" + item.getName() + "' saved successfully!");
                loadTable();
                selectItemInTable(code);
            } else {
                AlertUtil.showError("Save Failed", "Could not add item. Item code might already exist.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Invalid Input", "Please check all fields: " + e.getMessage());
        }
    }

    @FXML
    void btnBrowseOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Apparel Product Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg, *.webp)", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                loadedImageBytes = fis.readAllBytes();
                Image image = new Image(new ByteArrayInputStream(loadedImageBytes));
                updateImagePreview(image, loadedImageBytes, selectedFile.getName());

                // Immediately update selected item in the table so user sees the change right away!
                Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    selectedItem.setImage(loadedImageBytes);
                    tblItems.refresh();
                }
            } catch (Exception e) {
                AlertUtil.showError("File Error", "Failed to load selected image: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnClearImageOnAction(ActionEvent event) {
        loadedImageBytes = null;
        updateImagePreview(null, null, "");
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedItem.setImage(null);
            tblItems.refresh();
        }
    }

    @FXML
    public void btnClearOnAction(ActionEvent event) {
        txtSupplier.setText("");
        txtSize.setText("");
        txtName.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
        txtItemCode.setText("");
        cmbCategory.setValue(null);
        loadedImageBytes = null;
        updateImagePreview(null, null, "");
        tblItems.getSelectionModel().clearSelection();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String code = txtItemCode.getText().trim();
        if (code.isEmpty()) {
            AlertUtil.showWarning("Missing Code", "Please select an item to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Delete Product", "Delete product " + code + "?", "This will permanently remove the item from catalog.");
        if (confirm) {
            if (service.deleteItem(code)) {
                deleteImageFromDisk(code);
                AlertUtil.showSuccess("Item Deleted", "Product " + code + " removed successfully!");
                loadTable();
                btnClearOnAction(event);
            } else {
                AlertUtil.showError("Delete Failed", "Could not delete item.");
            }
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String code = txtItemCode.getText().trim();
        if (code.isEmpty()) {
            AlertUtil.showWarning("Search Input", "Please enter a product code to search.");
            return;
        }
        Item item = service.searchItem(code);
        if (item != null) {
            addValueToText(item);
            tblItems.getSelectionModel().select(item);
        } else {
            AlertUtil.showWarning("Not Found", "No apparel item found with code: " + code);
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try {
            String code = txtItemCode.getText() != null ? txtItemCode.getText().trim() : "";
            if (code.isEmpty()) {
                AlertUtil.showError("Validation Error", "Product Code cannot be empty.");
                return;
            }
            if (!ValidationUtil.isNotEmpty(txtName.getText())) {
                AlertUtil.showError("Validation Error", "Product Name cannot be empty.");
                return;
            }
            if (!ValidationUtil.isPositiveDouble(txtPrice.getText())) {
                AlertUtil.showError("Validation Error", "Price must be a valid positive number.");
                return;
            }
            if (!ValidationUtil.isPositiveInt(txtQuantity.getText())) {
                AlertUtil.showError("Validation Error", "Quantity must be a valid non-negative integer.");
                return;
            }

            Item item = new Item(
                    code,
                    txtName.getText().trim(),
                    txtSize.getText() != null ? txtSize.getText().trim() : "",
                    Double.parseDouble(txtPrice.getText().trim()),
                    Integer.parseInt(txtQuantity.getText().trim()),
                    cmbCategory.getValue() != null ? cmbCategory.getValue() : "Gents",
                    txtSupplier.getText() != null ? txtSupplier.getText().trim() : "",
                    loadedImageBytes
            );

            if (service.updateItem(item)) {
                if (loadedImageBytes != null && loadedImageBytes.length > 0) {
                    saveImageToDisk(code, loadedImageBytes);
                } else {
                    deleteImageFromDisk(code);
                }
                AlertUtil.showSuccess("Item Updated", "Product '" + item.getName() + "' updated successfully!");
                loadTable();
                selectItemInTable(code);
            } else {
                AlertUtil.showError("Update Failed", "Could not update item in database.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Invalid Input", "Please check all fields: " + e.getMessage());
        }
    }

    // =========================================================================
    // PERSISTENT DISK STORAGE FOR IMAGES (RELOADS CLEANLY ACROSS APP RESTARTS)
    // =========================================================================

    private void saveImageToDisk(String itemCode, byte[] data) {
        if (itemCode == null || data == null || data.length == 0) return;
        String[] candidatePaths = {
                STORAGE_DIR_PATH,
                "data/item_images",
                "clothify/data/item_images"
        };
        for (String base : candidatePaths) {
            try {
                File dir = new File(base);
                if (!dir.exists()) dir.mkdirs();
                File dest = new File(dir, itemCode + ".png");
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    fos.write(data);
                }
            } catch (Exception ignored) {}
        }
    }

    private byte[] loadImageFromDisk(String itemCode) {
        if (itemCode == null) return null;
        String[] candidatePaths = {
                STORAGE_DIR_PATH,
                "data/item_images",
                "clothify/data/item_images",
                "../data/item_images"
        };
        String[] exts = {".png", ".jpg", ".jpeg"};
        for (String base : candidatePaths) {
            for (String ext : exts) {
                File file = new File(base, itemCode + ext);
                if (file.exists() && file.isFile() && file.length() > 0) {
                    try {
                        return Files.readAllBytes(file.toPath());
                    } catch (Exception ignored) {}
                }
            }
        }
        return null;
    }

    private void deleteImageFromDisk(String itemCode) {
        if (itemCode == null) return;
        String[] candidatePaths = {
                STORAGE_DIR_PATH,
                "data/item_images",
                "clothify/data/item_images",
                "../data/item_images"
        };
        String[] exts = {".png", ".jpg", ".jpeg"};
        for (String base : candidatePaths) {
            for (String ext : exts) {
                File file = new File(base, itemCode + ext);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
