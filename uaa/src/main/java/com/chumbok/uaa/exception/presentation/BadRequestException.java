package com.chumbok.uaa.exception.presentation;


public class BadRequestException extends RuntimeException implements PresentationException {

    public BadRequestException(String message) {
        super(message);
    }

}
