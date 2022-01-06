package com.example.embroideryshop.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CategoryInUseException extends RuntimeException {
    CategoryInUseException() {
        super("Kategoria jest obecnie wykorzystywana przed inne produkty.");
    }
}
