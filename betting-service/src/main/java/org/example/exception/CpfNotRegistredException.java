package org.example.exception;

public class CpfNotRegistredException extends RuntimeException{
    public CpfNotRegistredException(String message) {
        super(message);
    }
}
