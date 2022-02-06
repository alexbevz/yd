package ru.bevz.yd.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleAnyException(Exception exception) {

        InfoException infoException = new InfoException();
        infoException.setValidation_error(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(infoException);
    }

    //TODO: Need to add a method for handling exception array couriers and contracts

}
