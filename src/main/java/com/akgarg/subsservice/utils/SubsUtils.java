package com.akgarg.subsservice.utils;

import com.akgarg.subsservice.exception.BadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.UUID;

public final class SubsUtils {

    private SubsUtils() {
        throw new IllegalStateException();
    }

    public static void checkValidationResultAndThrowExceptionOnFailure(
            final BindingResult validationResult
    ) {
        if (validationResult.hasFieldErrors()) {
            final String[] errors = validationResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList()
                    .toArray(String[]::new);

            throw new BadRequestException(errors, "Request Validation Failed");
        }
    }

    public static String generateLogId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
