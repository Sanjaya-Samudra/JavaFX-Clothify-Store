package test;
import controller.item.ItemController;
import model.Item;
import java.io.File;
import java.nio.file.Files;

public class TestRealImageInTable {
    public static void main(String[] args) throws Exception {
        ItemController controller = ItemController.getInstance();
        Item item = controller.searchItem("P001");
        File realImg = new File("src/main/resources/Images/logo-background-removed-nav.png");
        byte[] realBytes = Files.readAllBytes(realImg.toPath());
        item.setImage(realBytes);
        boolean ok = controller.updateItem(item);
        System.out.println("Updated P001 with real image (" + realBytes.length + " bytes): " + ok);

        // Also save to data/item_images/P001.png
        File dest = new File("data/item_images");
        if (!dest.exists()) dest.mkdirs();
        Files.write(new File(dest, "P001.png").toPath(), realBytes);
        System.out.println("Saved to disk mirror: true");
    }
}
