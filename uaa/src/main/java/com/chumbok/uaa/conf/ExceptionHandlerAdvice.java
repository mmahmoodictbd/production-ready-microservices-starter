package com.chumbok.uaa.conf;

import com.chumbok.exception.AbstractExceptionHandlerAdvice;
import com.chumbok.exception.ErrorResponse;
import com.chumbok.security.exception.AuthTokenConsumeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice extends AbstractExceptionHandlerAdvice {

    public ExceptionHandlerAdvice() {
        super();
        registerMapping(AuthTokenConsumeException.class, "UNAUTHORIZED_REQUEST", "Could not authenticate.", UNAUTHORIZED);
    }
    
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ErrorResponse getErrorResponse(final Throwable ex, final HttpServletResponse response) {
        return super.getErrorResponse(ex, response);
    }

}
