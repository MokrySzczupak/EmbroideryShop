package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotValidEmailFormatException extends ResponseStatusException {
    public NotValidEmailFormatException() {
        super(HttpStatus.BAD_REQUEST, "Niepoprawny format adresu email");
    }
}
