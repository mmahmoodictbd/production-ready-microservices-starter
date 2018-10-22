package com.chumbok.uaa.conf;

import com.chumbok.security.exception.AuthTokenConsumeException;
import com.chumbok.uaa.exception.AbstractExceptionHandlerAdvice;
import com.chumbok.uaa.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice extends AbstractExceptionHandlerAdvice {

    public ExceptionHandlerAdvice() {
        super();
        registerMapping(AuthTokenConsumeException.class, "FORBIDDEN", "Permission denied.", FORBIDDEN);
    }
    
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ErrorResponse getErrorResponse(final Throwable ex, final HttpServletResponse response) {
        return getErrorResponse(ex, response);
    }

}
