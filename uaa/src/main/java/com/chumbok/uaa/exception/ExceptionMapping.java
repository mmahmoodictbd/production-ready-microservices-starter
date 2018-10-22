package com.chumbok.uaa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ExceptionMapping {

    private final String code;
    private final String message;
    private final HttpStatus status;
}