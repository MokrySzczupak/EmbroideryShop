package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongQuantityException extends ResponseStatusException {
    public WrongQuantityException() {
        super(HttpStatus.BAD_REQUEST, "Niewłaściwa ilość produktów...");
    }
}
