package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO getEmployeeById(Long id);
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    void deleteEmployee(Long id);
    List<EmployeeDTO> getAllEmployees();
    List<EmployeeDTO> searchEmployee(String name);
    List<EmployeeDTO> getEmployeesByManager(Long managerId);
}