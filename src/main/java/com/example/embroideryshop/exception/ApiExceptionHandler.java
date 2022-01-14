package com.example.embroideryshop.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> handleApiException(ResponseStatusException e) {
        ApiException apiException = new ApiException(e.getReason(), e.getStatus(), ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, e.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, EmptyResultDataAccessException.class, NoSuchElementException.class,
            IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequestExceptions() {
        ApiException apiException = new ApiException("Zasób nie istnieje..", HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> handleSqlIntegrityConstraintViolationException() {
        ApiException apiException = new ApiException("Coś poszło nie tak, upewnij się że pola są uzupełnione...", HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

}
