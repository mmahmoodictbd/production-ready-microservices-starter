package com.chumbok.uaa.exception.presentation;


public class ForbiddenException extends RuntimeException implements PresentationException {

    public ForbiddenException(String message) {
        super(message);
    }

}
