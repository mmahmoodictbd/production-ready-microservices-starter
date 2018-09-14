package com.chumbok.uaa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Executes on successful and failed authentication, override this when needed.
 */
public interface AuthenticationHandler extends AuthenticationSuccessHandler, AuthenticationFailureHandler {

    /**
     * Run on successful authentication.
     * @param request
     * @param response
     * @param authentication
     */
    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    /**
     * Run on failed authentication.
     * @param request
     * @param response
     * @param ex
     */
    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex);

    /**
     * Run on successful logout.
     * @param request
     * @param response
     * @param authentication
     */
    void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}
