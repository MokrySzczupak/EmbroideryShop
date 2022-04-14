package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CartNotPaidException extends ResponseStatusException {
    public CartNotPaidException() {
        super(HttpStatus.PAYMENT_REQUIRED, "Nie można sfinalizować zamowienia ponieważ koszyk nie został opłacony");
    }
}
