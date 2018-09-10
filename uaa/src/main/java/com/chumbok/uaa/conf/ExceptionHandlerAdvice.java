package com.chumbok.uaa.conf;

import com.chumbok.uaa.exception.presentation.BadRequestException;
import com.chumbok.uaa.exception.presentation.InternalErrorException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final ExceptionMapping DEFAULT_ERROR =
            new ExceptionMapping("SERVER_ERROR", "Internal server error.", INTERNAL_SERVER_ERROR);

    private final Map<Class, ExceptionMapping> exceptionMappings = new HashMap<>();

    public ExceptionHandlerAdvice() {
        registerMapping(InternalErrorException.class, "SERVER_ERROR", "Internal server error.", BAD_REQUEST);
        registerMapping(BadRequestException.class, "INVALID_REQUEST", "Invalid request received.", INTERNAL_SERVER_ERROR);
        registerMapping(AuthenticationException.class, "UNAUTHORIZED_REQUEST", "Could not authenticate.", UNAUTHORIZED);
        registerMapping(NoHandlerFoundException.class, "RESOURCE_NOT_FOUND", "Could not find the resource.", NOT_FOUND);

    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ErrorResponse handleThrowable(final Throwable ex, final HttpServletResponse response) {

        ExceptionMapping mapping = exceptionMappings.get(ex.getClass());

        if (mapping == null) {
            mapping = findBaseExceptionMapping(ex, mapping);
            if (mapping == null) {
                mapping = DEFAULT_ERROR;
            }
        }

        log.error("{} ({}): {}", mapping.message, mapping.code, ex.getMessage(), ex);

        response.setStatus(mapping.status.value());
        return new ErrorResponse(mapping.code, mapping.message);
    }

    private ExceptionMapping findBaseExceptionMapping(Throwable ex, ExceptionMapping mapping) {
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

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {

        private final String code;
        private final String message;
    }

    @AllArgsConstructor
    private static class ExceptionMapping {

        private final String code;
        private final String message;
        private final HttpStatus status;
    }
}
