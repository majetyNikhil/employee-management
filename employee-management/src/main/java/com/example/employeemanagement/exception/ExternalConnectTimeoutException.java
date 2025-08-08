package com.example.employeemanagement.exception;

public class ExternalConnectTimeoutException extends RuntimeException {
    public ExternalConnectTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}