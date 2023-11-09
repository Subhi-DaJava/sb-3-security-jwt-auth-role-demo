package com.uyghrujava.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResponseStatusException extends RuntimeException {
    public ResponseStatusException(HttpStatus httpStatus, String message) {
        super(String.format("%s: %s", httpStatus, message));
    }
}
