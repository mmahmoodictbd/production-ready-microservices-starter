package com.chumbok.uaa.controller;


import com.chumbok.exception.presentation.BadRequestException;
import com.chumbok.testable.common.CookieUtil;
import com.chumbok.testable.common.UrlUtil;
import com.chumbok.uaa.security.AuthTokenBuilder;
import com.chumbok.uaa.security.AuthenticationHandler;
import com.chumbok.uaa.security.LoginRequest;
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
     *
     * @param authenticationManager the authentication manager
     * @param authenticationHandler the authentication handler
     * @param authTokenBuilder      the auth token builder
     * @param urlUtil               the url util
     * @param cookieUtil            the cookie util
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
     *
     * @param loginRequest the login request
     * @param request      the request
     * @param response     the response
     * @return map
     */
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Map<String, String> login(@RequestBody LoginRequest loginRequest,
                                     HttpServletRequest request, HttpServletResponse response) {

        String org = loginRequest.getOrg() != null ? loginRequest.getOrg() : "";
        String tenant = loginRequest.getTenant() != null ? loginRequest.getTenant() : "";
        String username = loginRequest.getUsername() != null ? loginRequest.getUsername() : "";
        String password = loginRequest.getPassword() != null ? loginRequest.getPassword() : "";

        if (StringUtils.isEmpty(org) || StringUtils.isEmpty(tenant)
                || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BadRequestException("Login request should contain org, tenant, username and password.");
        }

        UsernamePasswordAuthenticationToken authRequest =
                buildUsernamePasswordAuthenticationToken(org, tenant, username, password);

        log.debug("Authenticating user[{}] for org[{}] and tenant[{}]...", username, org, tenant);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authRequest);

            if (authentication == null || !authentication.isAuthenticated()
                    || (authentication instanceof AnonymousAuthenticationToken)) {
                throw new AuthenticationServiceException("Could not authenticate.");
            }
        } catch (AuthenticationException ex) {
            log.debug("Could not authenticate user[{}] for org[{}] and tenant[{}].", username, org, tenant);
            authenticationHandler.onAuthenticationFailure(request, response, ex);
            throw ex;
        }

        log.debug("user[{}] org[{}] and tenant[{}] is authenticated", username, org, tenant);

        String authToken = authTokenBuilder.createAccessToken(authentication);

        Cookie authCookie = cookieUtil.getCookie(AUTH_COOKIE_NAME, urlUtil.utf8Encode(authToken));
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);
        response.addCookie(authCookie);
        log.debug("Auth cookie is set for user[{}] org[{}] and tenant[{}]", username, org, tenant);

        clearAuthenticationAttributes(request);
        authenticationHandler.onAuthenticationSuccess(request, response, authentication);

        return Collections.singletonMap("accessToken", authToken);
    }


    private UsernamePasswordAuthenticationToken buildUsernamePasswordAuthenticationToken(
            String org, String tenant, String username, String password) {

        String authTokenUsername = org.trim()
                + String.valueOf(Character.LINE_SEPARATOR)
                + tenant.trim()
                + String.valueOf(Character.LINE_SEPARATOR)
                + username.trim();
        return new UsernamePasswordAuthenticationToken(authTokenUsername, password);
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     *
     * @param request the request
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }


}
