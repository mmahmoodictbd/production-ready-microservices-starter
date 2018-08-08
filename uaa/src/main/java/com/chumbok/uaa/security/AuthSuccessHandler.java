package com.chumbok.uaa.security;

import com.chumbok.uaa.security.token.JwtToken;
import com.chumbok.uaa.security.token.JwtTokenFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * On successful authentication, this handler write access token either in json body or as cookie.
 */
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    public static final String AUTH_COOKIE_NAME = "Authorization";

    private final JwtTokenFactory jwtTokenFactory;
    private final ObjectMapper mapper;

    public AuthSuccessHandler(final JwtTokenFactory jwtTokenFactory, final ObjectMapper mapper) {
        this.jwtTokenFactory = jwtTokenFactory;
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        User user = (User) authentication.getPrincipal();
        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());

        JwtToken accessToken = jwtTokenFactory.createAccessToken(user.getUsername(), authorities);
        JwtToken refreshToken = jwtTokenFactory.createRefreshToken(user.getUsername());

        if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())) {

            Map<String, String> tokenAttribs = new HashMap<>();
            tokenAttribs.put("accessToken", accessToken.getToken());
            tokenAttribs.put("refreshToken", refreshToken.getToken());

            response.getWriter().write(mapper.writeValueAsString(tokenAttribs));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        } else {
            response.addCookie(new Cookie(AUTH_COOKIE_NAME, accessToken.getToken()));
            response.setContentType(MediaType.TEXT_HTML_VALUE);
        }

        response.setStatus(HttpStatus.OK.value());

        clearAuthenticationAttributes(request);
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
