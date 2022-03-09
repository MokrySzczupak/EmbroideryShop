package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmptyCartException extends ResponseStatusException {
    public EmptyCartException() {
        super(HttpStatus.BAD_REQUEST, "Koszyk jest pusty");
    }
}
