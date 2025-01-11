package com.akgarg.subsservice.exception;

import lombok.Getter;

@Getter
public class SubscriptionException extends RuntimeException {

    private final String[] errors;
    private final int errorCode;
    private final String message;

    public SubscriptionException(final String[] errors, final int errorCode, String message) {
        this.errors = errors;
        this.errorCode = errorCode;
        this.message = message;
    }

}
