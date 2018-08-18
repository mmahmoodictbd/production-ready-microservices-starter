package com.chumbok.uaa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * On authentication failure, this handler set proper http status and write json response with message.
 */
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper;

    public AuthFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException ex) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Problem problem = Problem.builder()
                .withTitle("Authentication Problem")
                .withStatus(Status.UNAUTHORIZED)
                .withDetail(ex.getMessage()).build();
        response.getWriter().write(objectMapper.writeValueAsString(problem));

    }

}
