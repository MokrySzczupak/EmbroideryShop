package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CategoryInUseException extends ResponseStatusException {
    public CategoryInUseException() {
        super(HttpStatus.FORBIDDEN, "Kategoria jest obecnie wykorzystywana przed inne produkty.");
    }
}
