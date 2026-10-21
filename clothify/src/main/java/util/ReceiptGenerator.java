package util;

import model.Order;
import model.OrderDetail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    public static String generateFormattedTextReceipt(Order order) {
        StringBuilder sb = new StringBuilder();
        String line = "================================================\n";
        String dashed = "------------------------------------------------\n";

        sb.append(line);
        sb.append("               CLOTHIFY STORE                   \n");
        sb.append("        PREMIUM FASHION RETAIL & APPAREL        \n");
        sb.append("           Colombo, Western Province            \n");
        sb.append("             Tel: +94 11 234 5678               \n");
        sb.append(line);
        sb.append(String.format(" Receipt No : %-33s\n", order.getOrderId()));
        sb.append(String.format(" Date / Time: %s %s\n",
                order.getOrderDate() != null ? order.getOrderDate().toString() : "",
                order.getOrderTime() != null ? order.getOrderTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : ""));
        sb.append(String.format(" Cashier    : %-33s\n", order.getEmployeeName() != null ? order.getEmployeeName() : "Clothify POS"));
        sb.append(String.format(" Customer   : %-33s\n", order.getCustomerName() != null && !order.getCustomerName().isEmpty() ? order.getCustomerName() : "Walk-in Customer"));
        if (order.getCustomerEmail() != null && !order.getCustomerEmail().isEmpty()) {
            sb.append(String.format(" Email      : %-33s\n", order.getCustomerEmail()));
        }
        sb.append(String.format(" Payment    : %-33s\n", order.getPaymentType()));
        sb.append(dashed);
        sb.append(String.format(" %-20s %5s %9s %10s\n", "ITEM", "QTY", "PRICE", "TOTAL"));
        sb.append(dashed);

        if (order.getOrderDetails() != null) {
            for (OrderDetail d : order.getOrderDetails()) {
                String name = d.getItemName() != null ? d.getItemName() : "-";
                if (name.length() > 20) {
                    name = name.substring(0, 18) + "..";
                }
                sb.append(String.format(java.util.Locale.US, " %-20s %5d %,9.2f %,10.2f\n",
                        name, d.getQty(), d.getUnitPrice() != null ? d.getUnitPrice() : 0.0, d.getTotal() != null ? d.getTotal() : 0.0));
            }
        }

        sb.append(dashed);
        sb.append(String.format(java.util.Locale.US, " Sub Total                               : %,10.2f\n", order.getSubTotal() != null ? order.getSubTotal() : 0.0));
        sb.append(String.format(java.util.Locale.US, " Discount                                : %,10.2f\n", order.getDiscount() != null ? order.getDiscount() : 0.0));
        sb.append(line);
        sb.append(String.format(java.util.Locale.US, " NET TOTAL (LKR)                         : %,10.2f\n", order.getTotalCost() != null ? order.getTotalCost() : 0.0));
        sb.append(line);
        sb.append("               ||| | |||| | ||||| || |||        \n");
        sb.append(String.format("                    *%s*                        \n", order.getOrderId()));
        sb.append("         THANK YOU FOR SHOPPING AT CLOTHIFY!    \n");
        sb.append("     Items can be exchanged within 7 days with  \n");
        sb.append("            this original printed receipt.      \n");
        sb.append("               www.clothifystore.lk             \n");
        sb.append(line);

        return sb.toString();
    }

    public static File saveReceiptToFile(Order order) {
        String receiptText = generateFormattedTextReceipt(order);
        File receiptsDir = new File("receipts");
        if (!receiptsDir.exists()) {
            receiptsDir.mkdirs();
        }
        File file = new File(receiptsDir, order.getOrderId() + "_receipt.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(receiptText);
        } catch (IOException e) {
            System.err.println("Failed to write receipt file: " + e.getMessage());
        }
        return file;
    }
}
