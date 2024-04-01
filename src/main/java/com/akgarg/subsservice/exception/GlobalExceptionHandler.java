package com.akgarg.subsservice.exception;

import com.akgarg.subsservice.response.ApiErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.Map;

import static com.akgarg.subsservice.response.ApiErrorResponse.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(final BadRequestException e) {
        return ResponseEntity.badRequest().body(badRequestErrorResponse(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(final Exception e) {
        LOGGER.error(
                "Error in processing request: {}",
                Map.of("exception_class", e.getClass().getName(), "exception_msg", e.getMessage())
        );
        final ApiErrorResponse errorResponse = switch (e) {
            case HttpRequestMethodNotSupportedException ex ->
                    methodNotAllowedErrorResponse("Request HTTP method '" + ex.getMethod() + "' is not allowed. Allowed: " + Arrays.toString(ex.getSupportedMethods()));
            case HttpMediaTypeNotSupportedException ex ->
                    badRequestErrorResponse("Media type '" + ex.getContentType() + "' is not supported");
            case HttpMessageNotReadableException ex -> badRequestErrorResponse("Please provide valid request body");
            case NoResourceFoundException ex ->
                    resourceNotFoundErrorResponse("Requested resource not found: " + ex.getResourcePath());
            case MissingServletRequestParameterException ex ->
                    badRequestErrorResponse("Parameter '%s' of type %s is missing".formatted(ex.getParameterName(), ex.getParameterType()));
            default -> internalServerErrorResponse();
        };

        return ResponseEntity.status(errorResponse.getErrorCode())
                .body(errorResponse);
    }

}
