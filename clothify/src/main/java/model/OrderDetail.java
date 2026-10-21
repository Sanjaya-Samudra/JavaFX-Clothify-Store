package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetail {
    private Integer detailId;
    private String orderId;
    private String itemCode;
    private String itemName;
    private Double unitPrice;
    private Integer qty;
    private Double total;

    public OrderDetail(String orderId, String itemCode, String itemName, Double unitPrice, Integer qty, Double total) {
        this(null, orderId, itemCode, itemName, unitPrice, qty, total);
    }
}
