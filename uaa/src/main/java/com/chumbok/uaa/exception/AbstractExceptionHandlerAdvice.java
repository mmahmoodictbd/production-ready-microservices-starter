package com.chumbok.uaa.exception;

import com.chumbok.uaa.exception.presentation.BadRequestException;
import com.chumbok.uaa.exception.presentation.InternalErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
public abstract class AbstractExceptionHandlerAdvice {

    private static final ExceptionMapping DEFAULT_ERROR =
            new ExceptionMapping("SERVER_ERROR", "Internal server error.", INTERNAL_SERVER_ERROR);

    private final Map<Class, ExceptionMapping> exceptionMappings = new HashMap<>();

    public AbstractExceptionHandlerAdvice() {

        registerMapping(InternalErrorException.class,
                "SERVER_ERROR", "Internal server error.", INTERNAL_SERVER_ERROR);

        registerMapping(BadRequestException.class,
                "BAD_REQUEST", "Invalid request received.", BAD_REQUEST);

        registerMapping(AuthenticationException.class,
                "UNAUTHORIZED_REQUEST", "Could not authenticate.", UNAUTHORIZED);

        registerMapping(NoHandlerFoundException.class,
                "RESOURCE_NOT_FOUND", "Could not find the resource.", NOT_FOUND);

    }

    protected ErrorResponse getErrorResponse(final Throwable ex, final HttpServletResponse response) {

        ExceptionMapping mapping = findExceptionMapping(ex);

        log.error("{} ({}): {}", mapping.getMessage(), mapping.getCode(), ex.getMessage(), ex);

        response.setStatus(mapping.getStatus().value());
        return new ErrorResponse(mapping.getCode(), mapping.getMessage());
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

}
