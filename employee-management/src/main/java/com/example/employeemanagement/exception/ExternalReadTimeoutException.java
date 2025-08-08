package com.example.employeemanagement.exception;

public class ExternalReadTimeoutException extends RuntimeException {
    public ExternalReadTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}