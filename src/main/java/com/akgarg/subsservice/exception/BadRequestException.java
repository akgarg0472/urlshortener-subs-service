package com.akgarg.subsservice.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String[] errors;
    private final String message;

    public BadRequestException(final String[] errors, final String message) {
        this.errors = errors;
        this.message = message;
    }

}
