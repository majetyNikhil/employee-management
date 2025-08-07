package com.example.employeemanagement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CompanyEmailValidator implements ConstraintValidator<CompanyEmail, String> {

    @Override
    public void initialize(CompanyEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false;
        }
        return email.endsWith("@company.com");
    }
}