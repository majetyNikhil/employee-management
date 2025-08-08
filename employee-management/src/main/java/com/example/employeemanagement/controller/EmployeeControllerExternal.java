package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.ExternalConnectTimeoutException;
import com.example.employeemanagement.exception.ExternalReadTimeoutException;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.EmployeeServiceExternal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management External Info API", description = "APIs for managing employees external data")
public class EmployeeControllerExternal {

    private final EmployeeServiceExternal employeeServiceExternal;
    private final EmployeeService employeeService;

    @Operation(summary = "Fetch external info for employee")
    @GetMapping("/external/{id}")
    public ResponseEntity<?> fetchExternal(@PathVariable String id) {
        try {
            return ResponseEntity.ok(employeeServiceExternal.fetchExternalInfo(id));
        } catch (ResourceAccessException | ExternalReadTimeoutException | ExternalConnectTimeoutException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    @Operation(summary = "Send employee data to external service by employee ID")
    @PostMapping("/external/{id}")
    public ResponseEntity<?> postExternal(@PathVariable("id") Long employeeId) {
        try {
            EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId);
            return ResponseEntity.ok(employeeServiceExternal.postEmpoyeeToExternal(employeeDTO));
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ExternalReadTimeoutException | ExternalConnectTimeoutException | ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
}