package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Supplier {
    private String supplierId;
    private String name;
    private String company;
    private String email;
    private String contact;
    private String itemCategory;
    private LocalDateTime createdAt;

    public Supplier(String supplierId, String name, String company, String email, String contact, String itemCategory) {
        this(supplierId, name, company, email, contact, itemCategory, LocalDateTime.now());
    }
}
