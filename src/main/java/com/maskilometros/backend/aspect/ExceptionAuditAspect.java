package com.maskilometros.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ExceptionAuditAspect {

//    @AfterThrowing(value = "execution(* com.maskilometros.backend..controller..*(..)) ||" +
//            "execution(* com.maskilometros.backend..service..*(..))", throwing = "ex")
    public void logAfterException(JoinPoint joinPoint, Exception ex){
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();

        log.error("Exception ocurred in mehtod: {}",methodName);
        log.error("Arguments: {}", Arrays.toString(methodArgs));
        log.error("Exception type: {}", ex.getClass().getSimpleName());
        log.error("Exception message: {}", ex.getMessage());
    }
}
