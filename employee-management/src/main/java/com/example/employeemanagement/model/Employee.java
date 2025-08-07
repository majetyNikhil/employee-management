package com.example.employeemanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "manager")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String department;
    private String email;
    private Double salary;
    private String status;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;
}