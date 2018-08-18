package com.chumbok.uaa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Responsibility of this filter is to merge domain and username parameters into username, and pass it to authenticate.
 */
public class DomainUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String HTTP_REQUEST_PARAMETER_DOMAIN = "domain";

    private ObjectMapper objectMapper;

    public DomainUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Authenticate using merged domain and username as username
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest;

        if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())
                || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(request.getContentType())) {
            try {
                loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
            } catch (IOException e) {
                throw new AuthenticationServiceException("Could not read input.");
            }
        } else {
            loginRequest = LoginRequest.builder()
                    .domain(obtainDomain(request))
                    .username(obtainUsername(request))
                    .password(obtainPassword(request))
                    .build();
        }

        String domain = loginRequest.getDomain() == null ? "" : loginRequest.getDomain();
        String username = loginRequest.getUsername() == null ? "" : loginRequest.getUsername();
        String password = loginRequest.getPassword() == null ? "" : loginRequest.getPassword();

        if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new AuthenticationServiceException("Missing login request parameter(s). "
                    + "Must contain domain, username and password parameters.");
        }

        String domainUsername = String.format("%s%s%s", domain,
                String.valueOf(Character.LINE_SEPARATOR), username.trim());

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(domainUsername, password);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);

    }

    protected String obtainDomain(HttpServletRequest request) {
        return request.getParameter(HTTP_REQUEST_PARAMETER_DOMAIN);
    }

}
