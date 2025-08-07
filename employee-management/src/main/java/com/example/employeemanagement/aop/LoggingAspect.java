package com.example.employeemanagement.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.employeemanagement.service.*.*(..))")
    public void logBeforeServiceMethods(JoinPoint joinPoint) {
        logger.debug("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.example.employeemanagement.service.*.*(..))", returning = "result")
    public void logAfterServiceMethods(JoinPoint joinPoint, Object result) {
        logger.debug("Exiting method: {} with result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    @Around( "execution(* com.example.employeemanagement.service.*.*(..))")
    public Object logTimeTaken(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        logger.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), duration);
        return result;
    }
}