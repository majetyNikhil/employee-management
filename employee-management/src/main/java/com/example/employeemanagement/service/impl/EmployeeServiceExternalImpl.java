package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.exception.ExternalConnectTimeoutException;
import com.example.employeemanagement.exception.ExternalReadTimeoutException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeServiceExternal;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.TimeoutException;
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
    @CircuitBreaker(name = "externalServiceCB", fallbackMethod = "fallback")
    @Retry(name = "externalServiceRetry")
    public Mono<String> fetchExternalInfo(String query) {
        String url = externalServiceUrl + query;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(this::mapTimeouts)
                ;
    }

    @CircuitBreaker(name = "externalServiceCB", fallbackMethod = "fallback")
    @Retry(name = "externalServiceRetry")
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
                .bodyToMono(String.class)
                .onErrorMap(this::mapTimeouts);
    }

    private Throwable mapTimeouts(Throwable ex) {
        if (ex instanceof ConnectTimeoutException) {
            return new ExternalConnectTimeoutException("Connection to external service timed out", ex);
        }
        if (ex instanceof TimeoutException) {
            return new ExternalReadTimeoutException("External service did not respond in time", ex);
        }
        return ex;
    }

    @SuppressWarnings("unused")
    private Mono<String> fallback(String query, Throwable ex) {
        return Mono.error(ex);
    }

    @SuppressWarnings("unused")
    private Mono<String> fallback(EmployeeDTO employeeDTO, Throwable ex) {
        return Mono.error(ex);
    }

}