package com.maskilometros.backend.aspect;

import com.maskilometros.backend.dto.RegisterRequestDto;
import com.maskilometros.backend.exception.RegistrationValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterValidationAspect {

    private final CompromisedPasswordChecker compromisedPasswordChecker;

    @Before("execution(* com.maskilometros.backend.auth.controller.AuthController.register(..))")
    public void validateBeforeRegister(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        RegisterRequestDto requestDto = (RegisterRequestDto) args[0];

        log.info("Validating user registration request");

        Map<String, String> errors = new HashMap<>();

        //compromised password checker
        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(requestDto.password());

        if (decision.isCompromised()){
            errors.put("password","Choose a strong password");
        }

        if(!errors.isEmpty()){
            log.warn("Registration validation failed: {}", errors);
            throw new RegistrationValidationException(errors);
        }

        log.info("Registration validation passed");
    }
}
