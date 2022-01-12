package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CategoryAlreadyExistsException extends ResponseStatusException {
    public CategoryAlreadyExistsException(String category) {
        super(HttpStatus.CONFLICT, "Kategoria '" + category + "' już istnieje");
    }
}
