package ru.bevz.yd.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.bevz.yd.controller.IdList;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleAnyException(Exception exception) {

        InfoException infoException = new InfoException();
        infoException.setValidation_error(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(infoException);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotValidObjectsException(NotValidObjectsException exception) {

        InfoException infoException = new InfoException();
        Map<String, IdList> info = new HashMap<>();
        info.put(exception.getNameObjects(), exception.getIdList());
        infoException.setValidation_error(info);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(infoException);
    }

}
