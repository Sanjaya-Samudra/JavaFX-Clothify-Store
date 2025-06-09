package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Item {
    private String itemCode;
    private String name;
    private String size;
    private Double price;
    private Integer quantity;
    private String category;
    private String supplier;
    private byte[] image;
}
