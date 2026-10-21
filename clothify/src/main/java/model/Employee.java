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
public class Employee {
    private String employeeId;
    private String name;
    private String email;
    private String contact;
    private String company;
    private String role;
    private Double salary;
    private LocalDateTime createdAt;

    public Employee(String employeeId, String name, String email, String contact, String company, String role, Double salary) {
        this(employeeId, name, email, contact, company, role, salary, LocalDateTime.now());
    }
}
