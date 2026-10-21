package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderReturn {
    private String returnId;
    private String orderId;
    private String itemCode;
    private Integer returnQty;
    private String reason;
    private Double refundAmount;
    private LocalDate returnDate;
}
