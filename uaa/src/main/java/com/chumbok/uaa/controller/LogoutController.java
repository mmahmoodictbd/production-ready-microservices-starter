package com.chumbok.uaa.controller;

import com.chumbok.testable.common.CookieUtil;
import com.chumbok.uaa.security.AuthenticationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class LogoutController {

    private static final String AUTH_COOKIE_NAME = "Authorization";

    private final AuthenticationHandler authenticationHandler;
    private final CookieUtil cookieUtil;

    public LogoutController(final AuthenticationHandler authenticationHandler, final CookieUtil cookieUtil) {
        this.authenticationHandler = authenticationHandler;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) {

        if (authentication == null
                || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated()) {
            log.debug("User should be authenticated in order to logout");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        response.addCookie(emptyAuthenticationCookie());
        authenticationHandler.onLogoutSuccess(request, response, authentication);

        log.debug("User '{}' successfully logged out.", authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Create same cookie as original one, but only empty. So it can be replaced properly
     */
    private Cookie emptyAuthenticationCookie() {
        Cookie cookie = cookieUtil.getCookie(AUTH_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

}
