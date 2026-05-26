package com.maskilometros.backend.exception;

public class InvalidRaceStateException extends RuntimeException{

    public InvalidRaceStateException(String message) {
        super(message);
    }
}
