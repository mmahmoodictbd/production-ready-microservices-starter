package com.chumbok.blog.config;

import com.chumbok.exception.AbstractExceptionHandlerAdvice;
import com.chumbok.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice extends AbstractExceptionHandlerAdvice {

    public ExceptionHandlerAdvice() {
        super();
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ErrorResponse getErrorResponse(final Throwable ex, final HttpServletResponse response) {
        return super.getErrorResponse(ex, response);
    }

}
