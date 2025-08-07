package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.argThat;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee testEmployee;
    private EmployeeDTO testEmployeeDTO;
    private Employee testManager;

    @BeforeEach
    void setUp() {
        testManager = new Employee();
        testManager.setId(2L);
        testManager.setName("Manager Name");
        testManager.setDepartment("Management");
        testManager.setEmail("manager@example.com");
        testManager.setSalary(80000.0);
        testManager.setStatus("ACTIVE");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setName("John Doe");
        testEmployee.setDepartment("IT");
        testEmployee.setEmail("john@example.com");
        testEmployee.setSalary(50000.0);
        testEmployee.setStatus("ACTIVE");
        testEmployee.setManager(testManager);

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
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDTO result = employeeService.createEmployee(testEmployeeDTO);

        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals(testEmployee.getName(), result.getName());
        assertEquals("ACTIVE", result.getStatus());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_WithManager_Success() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDTO result = employeeService.createEmployee(testEmployeeDTO);

        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(2L, result.getManagerId());
        verify(employeeRepository).findById(2L);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_WithNonExistentManager_ThrowsException() {
        testEmployeeDTO.setManagerId(999L);
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> 
            employeeService.createEmployee(testEmployeeDTO)
        );
    }

    @Test
    void getEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        EmployeeDTO result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals(testEmployee.getName(), result.getName());
        verify(employeeRepository).findById(1L);
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.getEmployeeById(1L)
        );
    }

    @Test
    void getEmployeesByManager_Success() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByManager_Id(2L)).thenReturn(employees);

        List<EmployeeDTO> results = employeeService.getEmployeesByManager(2L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getManagerId());
        assertEquals("ACTIVE", results.get(0).getStatus());
    }

    @Test
    void getEmployeesByManager_FilterInactiveEmployees() {
        Employee inactiveEmployee = new Employee();
        inactiveEmployee.setId(3L);
        inactiveEmployee.setStatus("INACTIVE");
        inactiveEmployee.setManager(testManager);

        when(employeeRepository.findByManager_Id(2L))
                .thenReturn(Arrays.asList(testEmployee, inactiveEmployee));

        List<EmployeeDTO> results = employeeService.getEmployeesByManager(2L);

        assertEquals(1, results.size());
        assertEquals("ACTIVE", results.get(0).getStatus());
    }

    @Test
    void getAllEmployees_Success() {
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeDTO> results = employeeService.getAllEmployees();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testEmployee.getName(), results.get(0).getName());
        verify(employeeRepository).findAll();
    }

    @Test
    void updateEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDTO result = employeeService.updateEmployee(1L, testEmployeeDTO);

        assertNotNull(result);
        assertEquals(testEmployeeDTO.getName(), result.getName());
        assertEquals(testEmployeeDTO.getDepartment(), result.getDepartment());
        assertEquals(testEmployeeDTO.getEmail(), result.getEmail());
        assertEquals(testEmployeeDTO.getSalary(), result.getSalary());
        assertEquals(testEmployeeDTO.getStatus(), result.getStatus());
        assertEquals(testEmployeeDTO.getManagerId(), result.getManagerId());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateEmployee_WithManager_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDTO result = employeeService.updateEmployee(1L, testEmployeeDTO);

        assertNotNull(result);
        assertEquals(2L, result.getManagerId());
    }

    @Test
    void updateEmployee_RemoveManager_Success() {
        testEmployeeDTO.setManagerId(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        EmployeeDTO result = employeeService.updateEmployee(1L, testEmployeeDTO);

        assertNotNull(result);
    }

    @Test
    void updateEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.updateEmployee(1L, testEmployeeDTO)
        );
    }

    @Test
    void deleteEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).save(argThat(employee -> 
            "INACTIVE".equals(employee.getStatus())
        ));
    }

    @Test
    void deleteEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.deleteEmployee(1L)
        );
    }

    @Test
    void fetchExternalInfo_Success() {
        String query = "1";
        String expectedResponse = "{\"data\": \"test\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedResponse);

        String result = employeeService.fetchExternalInfo(query);

        assertEquals(expectedResponse, result);
    }

    @Test
    void fetchExternalInfo_WhenServiceUnavailable() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new ResourceAccessException("External service unavailable"));

        assertThrows(ResourceAccessException.class, () ->
            employeeService.fetchExternalInfo("1")
        );
    }

    @Test
    void searchEmployee_Success() {
        String searchName = "John";
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(employees);

        List<EmployeeDTO> results = employeeService.searchEmployee(searchName);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testEmployee.getName(), results.get(0).getName());
        verify(employeeRepository).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    void searchEmployee_NotFound() {
        String searchName = "NonExistent";
        when(employeeRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(Collections.emptyList());

        assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.searchEmployee(searchName)
        );
    }

    @Test
    void getAllEmployees_FiltersInactiveEmployees() {
        Employee inactiveEmployee = new Employee();
        inactiveEmployee.setId(3L);
        inactiveEmployee.setStatus("INACTIVE");
        
        when(employeeRepository.findAll())
                .thenReturn(Arrays.asList(testEmployee, inactiveEmployee));

        List<EmployeeDTO> results = employeeService.getAllEmployees();

        assertEquals(1, results.size());
        assertEquals("ACTIVE", results.get(0).getStatus());
        verify(employeeRepository).findAll();
    }

    @Test
    void createEmployee_WithNullValues_ThrowsException() {
        EmployeeDTO invalidDTO = new EmployeeDTO();
        when(employeeRepository.save(any(Employee.class)))
                .thenThrow(new IllegalArgumentException("Employee name cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> 
            employeeService.createEmployee(invalidDTO)
        );
    }

    @Test
    void updateEmployee_WithInvalidData_ThrowsException() {
        testEmployeeDTO.setSalary(-1000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));
        when(employeeRepository.save(any(Employee.class))).thenThrow(new IllegalArgumentException("Invalid salary value"));

        assertThrows(IllegalArgumentException.class, () ->
                employeeService.updateEmployee(1L, testEmployeeDTO)
        );
    }

    @Test
    void getAllEmployees_WhenRepositoryThrowsException() {
        when(employeeRepository.findAll())
                .thenThrow(new DataAccessException("Database connection failed") {});

        assertThrows(DataAccessException.class, () ->
            employeeService.getAllEmployees()
        );
    }

    @Test
    void searchEmployee_WithEmptyString() {
        String emptySearch = "";
        when(employeeRepository.findByNameContainingIgnoreCase(emptySearch))
                .thenReturn(Collections.emptyList());

        assertThrows(EmployeeNotFoundException.class, () ->
            employeeService.searchEmployee(emptySearch)
        );
    }
}