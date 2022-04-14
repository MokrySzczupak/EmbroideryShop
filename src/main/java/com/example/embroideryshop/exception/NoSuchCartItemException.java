package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchCartItemException extends ResponseStatusException {
    public NoSuchCartItemException() {
        super(HttpStatus.BAD_REQUEST, "Brak podanego produktu w koszyku");
    }
}
