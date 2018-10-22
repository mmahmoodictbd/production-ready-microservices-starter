package com.chumbok.uaa.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FilterExceptionHandlerFilter implements Filter {

    private AbstractExceptionHandlerAdvice exceptionHandlerAdvice;

    public FilterExceptionHandlerFilter(AbstractExceptionHandlerAdvice exceptionHandlerAdvice) {
        this.exceptionHandlerAdvice = exceptionHandlerAdvice;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (RuntimeException ex) {
            handleException((HttpServletResponse) response, ex);
        }
    }

    private void handleException(HttpServletResponse response, RuntimeException ex) throws IOException {

        ExceptionMapping mapping = exceptionHandlerAdvice.findExceptionMapping(ex);
        ErrorResponse errorResponse = new ErrorResponse(mapping.getCode(), mapping.getMessage());

        HttpServletResponse httpServletResponse = response;
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus((mapping.getStatus().value()));
        httpServletResponse.getWriter().write(convertObjectToJson(errorResponse));
    }


    protected String convertObjectToJson(ErrorResponse errorResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(errorResponse);
    }

    @Override
    public void destroy() {

    }
}
