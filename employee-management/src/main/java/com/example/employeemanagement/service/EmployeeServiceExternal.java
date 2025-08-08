package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmployeeServiceExternal {
    Mono<String> fetchExternalInfo(String query);

    Mono<String> postEmpoyeeToExternal(EmployeeDTO employeeDTO);
}