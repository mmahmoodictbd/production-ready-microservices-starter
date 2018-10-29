package com.chumbok.uaa.exception.presentation;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ValidationException extends RuntimeException implements PresentationException {

    private Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(BindingResult bindingResult) {

        super("Invalid request received.");

        if (!bindingResult.getFieldErrors().isEmpty()) {
            fieldErrors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
        }
    }
    
}
