package com.maskilometros.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAndPerformanceAspect {

    @Around("execution(* com.maskilometros.backend..controller..*(..)) || " +
            "execution(* com.maskilometros.backend..service..*(..)) ")
    public Object longAndMeasureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        //Before aspect logic
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("Entering method: {}", methodName);
        log.info("Arguments: {}", Arrays.toString(methodArgs));

        //Continue to taget method
        Object result = joinPoint.proceed();

        //After aspect logic
        long executionTime = System.currentTimeMillis()-startTime;
        log.info("Method executed successfully: {}",methodName);
        log.info("Execution time: {}", executionTime);
        return result;
    }
}
