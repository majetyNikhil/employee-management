package com.example.employeemanagement.dto;

import com.example.employeemanagement.validation.CompanyEmail;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private Long id;
    private String name;
    private String department;
    @CompanyEmail
    private String email;
    private Double salary;
    private String status;
    private Long managerId;
}