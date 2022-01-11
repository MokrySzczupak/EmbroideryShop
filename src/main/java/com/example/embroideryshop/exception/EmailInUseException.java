package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailInUseException extends RuntimeException{
    public EmailInUseException(String email) {
        super("Email '" + email + "' jest zajęty");
    }
}
