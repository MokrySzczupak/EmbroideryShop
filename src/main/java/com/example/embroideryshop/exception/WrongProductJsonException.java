package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongProductJsonException extends ResponseStatusException {
    public WrongProductJsonException() {
        super(HttpStatus.BAD_REQUEST, "Nie można utworzyć produktu");
    }
}
