package com.chumbok.uaa.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * This class just returns HTTP code 401 (Unauthorized) when authentication fails,
 * overriding default Spring’s redirecting
 */
@Component
public class AuthEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException, ServletException {

        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }
}
