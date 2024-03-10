package com.akgarg.subsservice.response;


import com.akgarg.subsservice.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static com.akgarg.subsservice.response.ApiErrorResponse.ApiErrorType.*;

@Getter
public final class ApiErrorResponse {

    private final String[] errors;

    @JsonProperty("status_code")
    private final int errorCode;

    @JsonProperty("error_message")
    private final String message;

    private ApiErrorResponse(final String[] errors, final ApiErrorType errorType, String message) {
        this.errors = errors;
        this.errorCode = errorType.code();
        this.message = message;
    }

    public static ApiErrorResponse methodNotAllowedErrorResponse(final String message) {
        return new ApiErrorResponse(new String[]{"Request Method not allowed"}, METHOD_NOT_ALLOWED, message);
    }

    public static ApiErrorResponse badRequestErrorResponse(final String errorMsg) {
        return new ApiErrorResponse(new String[]{errorMsg}, BAD_REQUEST, "Invalid Request");
    }

    public static ApiErrorResponse badRequestErrorResponse(final BadRequestException e) {
        return new ApiErrorResponse(e.getErrors(), BAD_REQUEST, e.getMessage());
    }

    public static ApiErrorResponse resourceNotFoundErrorResponse(final String message) {
        return new ApiErrorResponse(new String[]{message}, NOT_FOUND, "Not Found");
    }

    public static ApiErrorResponse internalServerErrorResponse() {
        return new ApiErrorResponse(new String[]{"Internal Server Error"}, INTERNAL_SERVER_ERROR, "Internal server error");
    }

    @SuppressWarnings("unused")
    public String[] getErrors() {
        return errors;
    }

    public enum ApiErrorType {

        BAD_REQUEST(400),
        INTERNAL_SERVER_ERROR(500),
        METHOD_NOT_ALLOWED(405),
        NOT_FOUND(404);

        private final int code;

        ApiErrorType(final int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }

}
