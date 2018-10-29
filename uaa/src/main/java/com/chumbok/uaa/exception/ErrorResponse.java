package com.chumbok.uaa.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorResponse {

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> invalidFields = new HashMap<>();

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, Map<String, String> invalidFields) {
        this.code = code;
        this.message = message;
        this.invalidFields = invalidFields;
    }
}
