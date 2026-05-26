package com.maskilometros.backend.aspect;

import com.maskilometros.backend.dto.LoginResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoginSuccesAuditAspect {

    @AfterReturning(pointcut = "execution(* com.maskilometros.backend.auth.controller.AuthController.login(..))", returning = "response")
    public void logSuccessLogin(JoinPoint joinPoint, Object response){

        //if the response is not ResponseEntity
        if(!(response instanceof ResponseEntity<?> responseEntity)){
            return;
        }

        //if the response is not LoginResponseDto
        Object body = responseEntity.getBody();
        if(!(body instanceof LoginResponseDto loginResponse)){
            return;
        }

        // Only log if login is really successful
        if(loginResponse.userDto()!= null){
            String username = loginResponse.userDto().getEmail();
            String role = loginResponse.userDto().getRole();
            log.info("LOGIN SUCCESS | User: {} | Role: {}", username, role);
        }
    }
}
