package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private String orderId;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String paymentType;  // Cash, Card, Online
    private Double subTotal;
    private Double discount;
    private Double totalCost;
    private String employeeId;
    private String employeeName;
    private String status;       // COMPLETED, RETURNED, CANCELLED

    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Order(String orderId, LocalDate orderDate, LocalTime orderTime, String customerName,
                 String customerEmail, String customerPhone, String paymentType, Double subTotal,
                 Double discount, Double totalCost, String employeeId, String employeeName, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.paymentType = paymentType;
        this.subTotal = subTotal;
        this.discount = discount;
        this.totalCost = totalCost;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.status = status;
        this.orderDetails = new ArrayList<>();
    }
}
