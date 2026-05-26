package com.maskilometros.backend.exception;

public class InvalidRaceDateException extends RuntimeException{

    public InvalidRaceDateException(String message) {
        super(message);
    }
}
