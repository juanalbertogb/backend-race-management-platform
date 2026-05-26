package com.maskilometros.backend.exception;

import com.maskilometros.backend.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        List<FieldError> errorList =ex.getBindingResult().getFieldErrors();
        errorList.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String,String>> handlerMethodValidation(HandlerMethodValidationException ex){
        Map<String, String> errors = new HashMap<>();
        List<ParameterValidationResult> results = ex.getParameterValidationResults();

        results.forEach(result -> {
            String paramName =result.getMethodParameter().getParameterName();

            String combinedMessages =result.getResolvableErrors()
                    .stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(","));

            errors.put(paramName, combinedMessages);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.UNAUTHORIZED.value(),
                "Invalid credentials");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDto);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDto> handleDisabledException(DisabledException ex, HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.FORBIDDEN.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponseDto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthentication(HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.UNAUTHORIZED.value(),
                "Authentication failed");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex,
                                                                 HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.FORBIDDEN.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponseDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex,
                                                                   HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.NOT_FOUND.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public  ResponseEntity<ErrorResponseDto> handleResourceAlreadyExists(ResourceAlreadyExistsException ex,
                                                                         HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.CONFLICT.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    @ExceptionHandler(InvalidRaceStateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRaceState(InvalidRaceStateException ex,
                                                                   HttpServletRequest httpServletRequest){
        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(InvalidRaceDateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRaceState(InvalidRaceDateException ex,
                                                                   HttpServletRequest httpServletRequest){
        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(RaceFullException.class)
    public ResponseEntity<String> handleRaceFull(RaceFullException ex){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentStateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPaymentState(InvalidPaymentStateException ex,
                                                                      HttpServletRequest httpServletRequest){
        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(RegistrationValidationException.class)
    public ResponseEntity<Map<String, String>> handleRegistrationValidation(RegistrationValidationException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrors());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(Exception ex,
                                                               HttpServletRequest httpServletRequest){

        ErrorResponseDto errorResponseDto = buildErrorResponse(httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    private ErrorResponseDto buildErrorResponse(HttpServletRequest httpServletRequest, int status, String message){
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(httpServletRequest.getRequestURI(),
                status, message, Instant.now(), null);
        return errorResponseDto;
    }
}
