package com.example.employeemanagement;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class EmployeeSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSeeder.class);

    private final EmployeeRepository employeeRepository;

    @Value("${app.seed-employees:false}")
    private boolean seedEmployees;

    public EmployeeSeeder(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEmployees) {
            logger.debug("Employee seeding skipped: app.seed-employees is false or not set.");
            return;
        }

        if (employeeRepository.count() > 0) {
            logger.info("Employee seeding skipped: employees already exist.");
            return;
        }

        logger.info("Starting initial database seeding for employees and managers...");

        List<Employee> managers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Employee manager = Employee.builder()
                    .name("Manager " + i)
                    .department("Management")
                    .email("manager" + i + "@example.com")
                    .salary(90000.0 + i * 5000)
                    .status("ACTIVE")
                    .manager(null)
                    .build();
            managers.add(manager);
        }
        managers = employeeRepository.saveAll(managers);

        Random random = new Random();

        List<Employee> employees = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Employee manager = managers.get(random.nextInt(managers.size()));
            Employee employee = Employee.builder()
                    .name("Employee " + i)
                    .department("Department " + ((i % 3) + 1))
                    .email("employee" + i + "@example.com")
                    .salary(40000.0 + i * 3500)
                    .status("ACTIVE")
                    .manager(manager)
                    .build();
            employees.add(employee);
        }
        employeeRepository.saveAll(employees);

        logger.info("Database seeding complete: 3 managers and 7 employees created.");
    }
}