package com.maskilometros.backend.exception;

public class RaceFullException extends RuntimeException{

    public RaceFullException(String message) {
        super(message);
    }
}
