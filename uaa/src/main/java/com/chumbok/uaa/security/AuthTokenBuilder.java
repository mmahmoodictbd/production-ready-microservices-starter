package com.chumbok.uaa.security;

import com.chumbok.security.EncryptionKeyUtil;
import com.chumbok.testable.common.DateUtil;
import com.chumbok.uaa.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates auth access tokens.
 */
@Slf4j
@Service
public class AuthTokenBuilder {

    private final AuthJwtProperties authJwtProperties;
    private final EncryptionKeyUtil encryptionKeyUtil;
    private final DateUtil dateUtil;
    private final JwtUtil jwtUtil;

    /**
     * Construct AuthTokenBuilder.
     *
     * @param authJwtProperties
     * @param dateUtil
     * @param encryptionKeyUtil
     */
    public AuthTokenBuilder(AuthJwtProperties authJwtProperties, EncryptionKeyUtil encryptionKeyUtil,
                            DateUtil dateUtil, JwtUtil jwtUtil) {
        this.authJwtProperties = authJwtProperties;
        this.encryptionKeyUtil = encryptionKeyUtil;
        this.dateUtil = dateUtil;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates access tokens from Authentication.
     * @param authentication
     * @return token.
     */
    public String createAccessToken(Authentication authentication) {

        String principal = (String) authentication.getPrincipal();

        if (StringUtils.isBlank(principal)) {
            throw new IllegalStateException("Authentication principle can not be null or empty.");
        }

        String[] domainAndUsername = StringUtils.split(principal, String.valueOf(Character.LINE_SEPARATOR));

        if (domainAndUsername == null || domainAndUsername.length != 2) {
            throw new IllegalStateException("Authentication principle[" + principal
                    + "] should contain domain and username.");
        }

        String domain = domainAndUsername[0];
        String username = domainAndUsername[1];
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());

        if (StringUtils.isBlank(domain)) {
            throw new IllegalArgumentException("Authentication principle[" + principal
                    + "] does not contain domain.");
        }

        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Authentication principle[" + principal
                    + "] does not contain username.");
        }

        if (authorities == null || authorities.isEmpty()) {
            throw new IllegalArgumentException("Authentication principle[" + principal
                    + "] does not contain authorities.");
        }

        Claims claims = Jwts.claims();
        claims.setSubject(username);
        claims.put("domain", domain);
        claims.put("scopes", authorities.stream().map(s -> s.toString()).collect(Collectors.toList()));

        LocalDateTime currentTime = dateUtil.getCurrentLocalDateTime();
        Date issueDate = Date.from(currentTime.toInstant(ZoneOffset.UTC));
        Date expiration = Date.from(currentTime.plusSeconds(
                authJwtProperties.getTokenExpirationTimeInSecond()).toInstant(ZoneOffset.UTC));

        PrivateKey privateKey = encryptionKeyUtil.loadPrivateKey(authJwtProperties.getTokenSigningPrivateKeyPath());

        return jwtUtil.getJwts(claims, authJwtProperties.getTokenIssuer(), issueDate, expiration, privateKey);

    }

}
