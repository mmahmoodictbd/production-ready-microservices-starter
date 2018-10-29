package com.chumbok.uaa.exception.presentation;

public class ResourceNotFoundException extends RuntimeException implements PresentationException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
