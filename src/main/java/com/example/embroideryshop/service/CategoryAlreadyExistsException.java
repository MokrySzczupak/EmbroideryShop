package com.example.embroideryshop.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String category) {
        super("Kategoria '" + category + "' już istnieje");
    }
}
