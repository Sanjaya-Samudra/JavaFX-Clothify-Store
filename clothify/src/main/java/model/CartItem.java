package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartItem {
    private String itemCode;
    private String name;
    private String category;
    private String size;
    private Double unitPrice;
    private Integer quantity;
    private Double total;
    private Integer availableStock;

    public void updateQuantity(int newQty) {
        this.quantity = newQty;
        this.total = this.unitPrice * newQty;
    }
}
