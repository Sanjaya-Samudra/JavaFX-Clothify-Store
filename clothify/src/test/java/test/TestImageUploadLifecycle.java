package test;
import controller.item.ItemController;
import model.Item;
import java.io.File;
import java.nio.file.Files;

public class TestImageUploadLifecycle {
    public static void main(String[] args) throws Exception {
        ItemController controller = ItemController.getInstance();
        Item item = controller.searchItem("P001");
        System.out.println("P001 before: hasImage=" + (item.getImage() != null && item.getImage().length > 0));

        // Let's create dummy test image bytes (PNG header)
        byte[] testBytes = new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4, (byte)0x89
        };

        item.setImage(testBytes);
        boolean updated = controller.updateItem(item);
        System.out.println("Update item in DB: " + updated);

        Item reloaded = controller.searchItem("P001");
        System.out.println("P001 reloaded: hasImage=" + (reloaded.getImage() != null && reloaded.getImage().length > 0) + ", bytesLength=" + (reloaded.getImage() != null ? reloaded.getImage().length : 0));
    }
}
