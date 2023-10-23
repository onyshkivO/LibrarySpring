package com.onyshkiv.libraryspring.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(MyException exception) {
        ErrorResponse personErrorResponse = new ErrorResponse(exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(MissingPathVariableException exception) {
        ErrorResponse personErrorResponse = new ErrorResponse(exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException exception) {
        ErrorResponse personErrorResponse = new ErrorResponse(exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

}
