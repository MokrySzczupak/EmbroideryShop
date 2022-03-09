package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchProductException extends ResponseStatusException {
    public NoSuchProductException() {
        super(HttpStatus.BAD_REQUEST, "Produkt nie istnieje...");
    }
}
