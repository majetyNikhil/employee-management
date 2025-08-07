package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeDTO testEmployeeDTO;
    private EmployeeDTO testManagerDTO;

    @BeforeEach
    void setUp() {
        testManagerDTO = new EmployeeDTO();
        testManagerDTO.setId(2L);
        testManagerDTO.setName("Manager Name");
        testManagerDTO.setDepartment("Management");
        testManagerDTO.setEmail("manager@example.com");
        testManagerDTO.setSalary(80000.0);
        testManagerDTO.setStatus("ACTIVE");

        testEmployeeDTO = new EmployeeDTO();
        testEmployeeDTO.setId(1L);
        testEmployeeDTO.setName("John Doe");
        testEmployeeDTO.setDepartment("IT");
        testEmployeeDTO.setEmail("john@example.com");
        testEmployeeDTO.setSalary(50000.0);
        testEmployeeDTO.setStatus("ACTIVE");
        testEmployeeDTO.setManagerId(2L);
    }

    @Test
    void createEmployee_Success() {
        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(testEmployeeDTO);

        ResponseEntity<?> response = employeeController.createEmployee(testEmployeeDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEmployeeDTO, response.getBody());
        verify(employeeService).createEmployee(any(EmployeeDTO.class));
    }

    @Test
    void createEmployee_WithInvalidData() {
        when(employeeService.createEmployee(any(EmployeeDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid employee data"));

        ResponseEntity<?> response = employeeController.createEmployee(new EmployeeDTO());
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid employee data", response.getBody());
    }

    @Test
    void createEmployee_WithManager() {
        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(testEmployeeDTO);

        ResponseEntity<?> response = employeeController.createEmployee(testEmployeeDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        EmployeeDTO responseBody = (EmployeeDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals(2L, responseBody.getManagerId());
    }

    @Test
    void getEmployee_Success() {
        when(employeeService.getEmployeeById(anyLong())).thenReturn(testEmployeeDTO);

        ResponseEntity<?> response = employeeController.getEmployee(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEmployeeDTO, response.getBody());
        verify(employeeService).getEmployeeById(1L);
    }

    @Test
    void getEmployee_WhenNotFound() {
        when(employeeService.getEmployeeById(anyLong()))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        ResponseEntity<?> response = employeeController.getEmployee(999L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody());
    }

    @Test
    void getAllEmployees_Success() {
        List<EmployeeDTO> employees = Arrays.asList(testEmployeeDTO);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ResponseEntity<?> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(employees, response.getBody());
        verify(employeeService).getAllEmployees();
    }

    @Test
    void getAllEmployees_WhenDatabaseError() {
        when(employeeService.getAllEmployees())
                .thenThrow(new DataAccessException("Database connection failed") {});

        ResponseEntity<?> response = employeeController.getAllEmployees();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error occurred", response.getBody());
    }

    @Test
    void updateEmployee_Success() {
        when(employeeService.updateEmployee(anyLong(), any(EmployeeDTO.class))).thenReturn(testEmployeeDTO);

        ResponseEntity<?> response = employeeController.updateEmployee(1L, testEmployeeDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEmployeeDTO, response.getBody());
        verify(employeeService).updateEmployee(1L, testEmployeeDTO);
    }

    @Test
    void updateEmployee_WhenEmployeeNotFound() {
        when(employeeService.updateEmployee(anyLong(), any(EmployeeDTO.class)))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        ResponseEntity<?> response = employeeController.updateEmployee(999L, testEmployeeDTO);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody());
    }

    @Test
    void deleteEmployee_Success() {
        doNothing().when(employeeService).deleteEmployee(anyLong());

        ResponseEntity<?> response = employeeController.deleteEmployee(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void deleteEmployee_WhenNotFound() {
        doThrow(new EmployeeNotFoundException("Employee not found"))
                .when(employeeService).deleteEmployee(anyLong());

        ResponseEntity<?> response = employeeController.deleteEmployee(999L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody());
    }

    @Test
    void fetchExternal_Success() {
        String expectedResponse = "{\"data\": \"test\"}";
        when(employeeService.fetchExternalInfo(anyString())).thenReturn(expectedResponse);

        ResponseEntity<?> response = employeeController.fetchExternal("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(employeeService).fetchExternalInfo("1");
    }

    @Test
    void fetchExternal_WhenServiceUnavailable() {
        when(employeeService.fetchExternalInfo(anyString()))
                .thenThrow(new ResourceAccessException("External service unavailable"));

        ResponseEntity<?> response = employeeController.fetchExternal("1");
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("External service unavailable", response.getBody());
    }

    @Test
    void searchEmployees_Success() {
        List<EmployeeDTO> employees = Arrays.asList(testEmployeeDTO);
        when(employeeService.searchEmployee(anyString())).thenReturn(employees);

        ResponseEntity<?> response = employeeController.searchEmployees("John");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(employees, response.getBody());
        verify(employeeService).searchEmployee("John");
    }

    @Test
    void searchEmployees_WhenNoMatches() {
        when(employeeService.searchEmployee(anyString()))
                .thenThrow(new EmployeeNotFoundException("No employees found"));

        ResponseEntity<?> response = employeeController.searchEmployees("NonExistent");
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No employees found", response.getBody());
    }

    @Test
    void getEmployeesByManager_Success() {
        List<EmployeeDTO> employees = Arrays.asList(testEmployeeDTO);
        when(employeeService.getEmployeesByManager(anyLong())).thenReturn(employees);

        ResponseEntity<?> response = employeeController.getEmployeesByManager(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(employees, response.getBody());
        verify(employeeService).getEmployeesByManager(2L);
    }

    @Test
    void getEmployeesByManager_WhenNoEmployeesFound() {
        when(employeeService.getEmployeesByManager(anyLong()))
                .thenThrow(new EmployeeNotFoundException("No employees found for manager"));

        ResponseEntity<?> response = employeeController.getEmployeesByManager(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No employees found for manager", response.getBody());
    }

    @Test
    void getEmployeesByManager_WhenManagerNotFound() {
        when(employeeService.getEmployeesByManager(anyLong()))
                .thenThrow(new EmployeeNotFoundException("Manager not found"));

        ResponseEntity<?> response = employeeController.getEmployeesByManager(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Manager not found", response.getBody());
    }
}