package com.example.employeemanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = CompanyEmailValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CompanyEmail {
    String message() default "Email must be a company email (@company.com)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}