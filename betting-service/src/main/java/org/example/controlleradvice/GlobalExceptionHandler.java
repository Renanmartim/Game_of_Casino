package org.example.controlleradvice;

import org.example.exception.CpfNotRegistredException;
import org.example.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CpfNotRegistredException.class)
    public Mono<ResponseEntity<Object>> handleInvalidCepException(CpfNotRegistredException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(400, ex.getMessage(), "Enter a valid CPF or register!");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }
}