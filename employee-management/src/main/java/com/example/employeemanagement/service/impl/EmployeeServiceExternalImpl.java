package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeServiceExternal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmployeeServiceExternalImpl implements EmployeeServiceExternal {
    private final WebClient webClient;
    @Value("${json.service.url}")
    private String externalServiceUrl;

    @Override
    public Mono<String> fetchExternalInfo(String query) {
        String url = externalServiceUrl + query;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> postEmpoyeeToExternal(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .name(employeeDTO.getName())
                .department(employeeDTO.getDepartment())
                .email(employeeDTO.getEmail())
                .salary(employeeDTO.getSalary())
                .status(employeeDTO.getStatus())
                .build();
        return webClient.post()
                .uri(externalServiceUrl)
                .bodyValue(employee)
                .retrieve()
                .bodyToMono(String.class);
    }
}