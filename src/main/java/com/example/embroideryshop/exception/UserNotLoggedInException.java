package com.example.embroideryshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotLoggedInException extends ResponseStatusException {
    public UserNotLoggedInException() {
        super(HttpStatus.FORBIDDEN, "Nie jesteś zalogowany");
    }
}
