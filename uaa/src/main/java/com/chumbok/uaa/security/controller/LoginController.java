package com.chumbok.uaa.security.controller;


import com.chumbok.testable.common.CookieUtil;
import com.chumbok.testable.common.UrlUtil;
import com.chumbok.uaa.security.LoginRequest;
import com.chumbok.uaa.exception.presentation.BadRequestException;
import com.chumbok.uaa.security.AuthTokenBuilder;
import com.chumbok.uaa.security.AuthenticationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Handle login
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    private static final String AUTH_COOKIE_NAME = "Authorization";

    private final AuthenticationManager authenticationManager;
    private final AuthenticationHandler authenticationHandler;
    private final AuthTokenBuilder authTokenBuilder;

    private final UrlUtil urlUtil;
    private final CookieUtil cookieUtil;

    /**
     * LoginController constructor with necessary dependencies.
     * @param authenticationManager
     * @param authenticationHandler
     * @param authTokenBuilder
     * @param urlUtil
     * @param cookieUtil
     */
    public LoginController(final AuthenticationManager authenticationManager,
                           final AuthenticationHandler authenticationHandler,
                           final AuthTokenBuilder authTokenBuilder,
                           final UrlUtil urlUtil, final CookieUtil cookieUtil) {
        this.authenticationManager = authenticationManager;
        this.authenticationHandler = authenticationHandler;
        this.authTokenBuilder = authTokenBuilder;
        this.urlUtil = urlUtil;
        this.cookieUtil = cookieUtil;
    }

    /**
     * Handle login request.
     * @param loginRequest
     * @param request
     * @param response
     * @return
     */
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Map<String, String> login(@RequestBody LoginRequest loginRequest,
                                     HttpServletRequest request, HttpServletResponse response) {

        String domain = loginRequest.getDomain() != null ? loginRequest.getDomain() : "";
        String username = loginRequest.getUsername() != null ? loginRequest.getUsername() : "";
        String password = loginRequest.getPassword() != null ? loginRequest.getPassword() : "";

        if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BadRequestException("Login request should contain domain, username and password.");
        }

        UsernamePasswordAuthenticationToken authRequest =
                buildUsernamePasswordAuthenticationToken(domain, username, password);

        log.debug("Authenticating user[{}] for domain[{}]...", username, domain);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authRequest);

            if (authentication == null || !authentication.isAuthenticated()
                    || (authentication instanceof AnonymousAuthenticationToken)) {
                throw new AuthenticationServiceException("Could not authenticate.");
            }
        } catch (AuthenticationException ex) {
            log.debug("Could not authenticate user[{}] for domain[{}].", username, domain);
            authenticationHandler.onAuthenticationFailure(request, response, ex);
            throw ex;
        }

        log.debug("user[{}] for domain[{}] is authenticated", username, domain);

        String authToken = authTokenBuilder.createAccessToken(authentication);

        Cookie authCookie = cookieUtil.getCookie(AUTH_COOKIE_NAME, urlUtil.utf8Encode(authToken));
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);
        response.addCookie(authCookie);

        clearAuthenticationAttributes(request);
        authenticationHandler.onAuthenticationSuccess(request, response, authentication);

        return Collections.singletonMap("accessToken", authToken);
    }


    private UsernamePasswordAuthenticationToken buildUsernamePasswordAuthenticationToken(
            String domain, String username, String password) {

        String domainUsername = String.format("%s%s%s", domain.trim(),
                String.valueOf(Character.LINE_SEPARATOR), username.trim());
        return new UsernamePasswordAuthenticationToken(domainUsername, password);
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }


}
