package com.chumbok.uaa.exception;

import com.chumbok.uaa.exception.presentation.BadRequestException;
import com.chumbok.uaa.exception.presentation.ForbiddenException;
import com.chumbok.uaa.exception.presentation.InternalErrorException;
import com.chumbok.uaa.exception.presentation.PresentationException;
import com.chumbok.uaa.exception.presentation.ResourceNotFoundException;
import com.chumbok.uaa.exception.presentation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
public abstract class AbstractExceptionHandlerAdvice {

    private static final ExceptionMapping DEFAULT_ERROR =
            new ExceptionMapping("SERVER_ERROR", "Internal server error.", INTERNAL_SERVER_ERROR);

    private final Map<Class, ExceptionMapping> exceptionMappings = new HashMap<>();

    public AbstractExceptionHandlerAdvice() {

        registerMapping(AuthenticationException.class,
                "UNAUTHORIZED_REQUEST", "Could not authenticate.", UNAUTHORIZED);

        registerMapping(NoHandlerFoundException.class,
                "RESOURCE_NOT_FOUND", "Could not find the resource.", NOT_FOUND);

        registerMapping(InternalErrorException.class,
                "SERVER_ERROR", "Internal server error.", INTERNAL_SERVER_ERROR);

        registerMapping(BadRequestException.class,
                "BAD_REQUEST", "Invalid request received.", BAD_REQUEST);

        registerMapping(ResourceNotFoundException.class,
                "RESOURCE_NOT_FOUND", "Could not find the resource.", NOT_FOUND);

        registerMapping(ForbiddenException.class,
                "FORBIDDEN_REQUEST", "User does not have enough permission.", FORBIDDEN);

        registerMapping(ValidationException.class,
                "BAD_REQUEST", "Invalid request received.", BAD_REQUEST);
    }

    protected ErrorResponse getErrorResponse(final Throwable ex, final HttpServletResponse response) {

        ExceptionMapping mapping = findExceptionMapping(ex);

        log.error("{} ({}): {}", mapping.getMessage(), mapping.getCode(), ex.getMessage(), ex);

        response.setStatus(mapping.getStatus().value());

        ErrorResponse errorResponse;
        if (isValidationException(ex) && ((ValidationException) ex).getFieldErrors() != null) {
            errorResponse = new ErrorResponse(mapping.getCode(), mapping.getMessage(),
                    ((ValidationException) ex).getFieldErrors());
        } else {
            if (isPresentationException(ex)) {
                errorResponse = new ErrorResponse(mapping.getCode(), ex.getMessage());
            } else {
                errorResponse = new ErrorResponse(mapping.getCode(), mapping.getMessage());
            }
        }

        return errorResponse;
    }

    public ExceptionMapping findExceptionMapping(Throwable ex) {

        ExceptionMapping mapping = exceptionMappings.get(ex.getClass());

        if (mapping == null) {
            mapping = findBaseExceptionMapping(ex, mapping);
            if (mapping == null) {
                mapping = DEFAULT_ERROR;
            }
        }

        return mapping;
    }

    protected ExceptionMapping findBaseExceptionMapping(Throwable ex, ExceptionMapping mapping) {

        for (Class cls : exceptionMappings.keySet()) {
            if (cls.isAssignableFrom(ex.getClass())) {
                mapping = exceptionMappings.get(cls);
                break;
            }
        }
        return mapping;
    }

    protected void registerMapping(final Class<?> clazz, final String code, final String message, final HttpStatus status) {
        exceptionMappings.put(clazz, new ExceptionMapping(code, message, status));
    }

    private boolean isPresentationException(Throwable ex) {
        return PresentationException.class.isAssignableFrom(ex.getClass());
    }

    private boolean isValidationException(Throwable ex) {
        return ValidationException.class.isAssignableFrom(ex.getClass());
    }
}
