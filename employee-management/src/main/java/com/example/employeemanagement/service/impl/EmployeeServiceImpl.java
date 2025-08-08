package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .name(employeeDTO.getName())
                .department(employeeDTO.getDepartment())
                .email(employeeDTO.getEmail())
                .salary(employeeDTO.getSalary())
                .status("ACTIVE")
                .build();
        if(employeeDTO.getManagerId() != null) {
            Optional<Employee> manager = employeeRepository.findById(employeeDTO.getManagerId()).or(() -> {
                throw new EmployeeNotFoundException("Manager not found with id: " + employeeDTO.getManagerId());
            });
            employee.setManager(manager.get());
            employeeDTO.setManagerId(manager.get().getId());
        }
        employee = employeeRepository.save(employee);
        employeeDTO.setId(employee.getId());
        employeeDTO.setStatus("ACTIVE");
        return employeeDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        return EmployeeDTO.builder()
                .id(emp.getId())
                .name(emp.getName())
                .department(emp.getDepartment())
                .email(emp.getEmail())
                .salary(emp.getSalary())
                .status(emp.getStatus())
                .managerId(emp.getManager() != null ? emp.getManager().getId() : null)
                .build();
    }



    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        emp.setName(employeeDTO.getName());
        emp.setDepartment(employeeDTO.getDepartment());
        emp.setEmail(employeeDTO.getEmail());
        emp.setSalary(employeeDTO.getSalary());
        emp.setStatus(employeeDTO.getStatus());
        emp.setManager(employeeDTO.getManagerId() != null ? employeeRepository.findById(employeeDTO.getManagerId()).orElseThrow(() -> new EmployeeNotFoundException("Manager not found with id: " + employeeDTO.getManagerId())) : null);
        emp = employeeRepository.save(emp);
        BeanUtils.copyProperties(emp, employeeDTO);
        return employeeDTO;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        emp.setStatus("INACTIVE");
        employeeRepository.save(emp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getStatus().equals("ACTIVE"))
                .map(emp -> EmployeeDTO.builder()
                        .id(emp.getId())
                        .name(emp.getName())
                        .department(emp.getDepartment())
                        .email(emp.getEmail())
                        .salary(emp.getSalary())
                        .status(emp.getStatus())
                        .managerId(emp.getManager() != null ? emp.getManager().getId() : null)
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> searchEmployee(String name) {
        List<Employee> emp = employeeRepository.findByNameContainingIgnoreCase(name);
        if (emp.isEmpty()) {
            throw new EmployeeNotFoundException("Employee not found with name: " + name);
        }
        return emp.stream().map(e -> EmployeeDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .department(e.getDepartment())
                .email(e.getEmail())
                .salary(e.getSalary())
                .status(e.getStatus())
                .managerId(e.getManager() != null ? e.getManager().getId() : null)
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByManager(Long managerId) {
        return employeeRepository.findByManager_Id(managerId).stream()
                .filter(emp -> emp.getStatus().equals("ACTIVE"))
                .map(emp -> EmployeeDTO.builder()
                        .id(emp.getId())
                        .name(emp.getName())
                        .department(emp.getDepartment())
                        .email(emp.getEmail())
                        .salary(emp.getSalary())
                        .status(emp.getStatus())
                        .managerId(emp.getManager().getId())
                        .build())
                .collect(Collectors.toList());
    }
}