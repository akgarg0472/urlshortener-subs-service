package com.akgarg.subsservice.utils;

import com.akgarg.subsservice.exception.BadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class SubsUtils {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

    private SubsUtils() {
        throw new IllegalStateException("utility class");
    }

    public static void checkValidationResultAndThrowExceptionOnFailure(final BindingResult validationResult) {
        if (validationResult.hasFieldErrors()) {
            final var errors = validationResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList()
                    .toArray(String[]::new);
            throw new BadRequestException(errors, "Request Validation Failed");
        }
    }

    public static String toUtcString(final long milliseconds) {
        final var instant = Instant.ofEpochMilli(milliseconds);
        return UTC_FORMATTER.format(instant);
    }

}
