package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailInUseException extends ResponseStatusException {
    public EmailInUseException(String email) {
        super(HttpStatus.CONFLICT, "Email '" + email + "' jest zajęty");
    }
}
