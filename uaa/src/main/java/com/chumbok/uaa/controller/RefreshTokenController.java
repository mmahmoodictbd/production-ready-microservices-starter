package com.chumbok.uaa.controller;

import com.chumbok.testable.common.CookieUtil;
import com.chumbok.testable.common.UrlUtil;
import com.chumbok.uaa.security.AuthTokenBuilder;
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
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The Refresh token controller.
 */
@Slf4j
@RestController
public class RefreshTokenController {

    private static final String AUTH_COOKIE_NAME = "Authorization";


    private final CookieUtil cookieUtil;
    private final UrlUtil urlUtil;
    private final AuthTokenBuilder authTokenBuilder;

    /**
     * Instantiates a new Refresh token controller.
     *
     * @param authTokenBuilder the auth token builder
     * @param cookieUtil       the cookie util
     * @param urlUtil          the url util
     */
    public RefreshTokenController(final AuthTokenBuilder authTokenBuilder,
                                  final CookieUtil cookieUtil, final UrlUtil urlUtil) {

        this.cookieUtil = cookieUtil;
        this.urlUtil = urlUtil;
        this.authTokenBuilder = authTokenBuilder;
    }

    /**
     * Handle refresh token request.
     *
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @return the response entity
     */
    @GetMapping(value = "/refresh", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response,
                                                       Authentication authentication) {

        if (authentication == null
                || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated()) {
            log.debug("User should be authenticated in order to refresh token");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String authToken = authTokenBuilder.createAccessToken(authentication);
        Cookie authCookie = cookieUtil.getCookie(AUTH_COOKIE_NAME,
                urlUtil.utf8Encode("Bearer " + authToken));
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);
        response.addCookie(authCookie);

        return new ResponseEntity<>(Collections.singletonMap("accessToken", authToken), HttpStatus.OK);
    }


}
