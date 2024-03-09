package com.akgarg.subsservice.exception;

import lombok.Getter;

@Getter
public class SubsException extends RuntimeException {

    private final String[] errors;
    private final int errorCode;
    private final String message;

    public SubsException(final String[] errors, final int errorCode, String message) {
        this.errors = errors;
        this.errorCode = errorCode;
        this.message = message;
    }

}
