package com.chumbok.uaa.security.token;

import com.chumbok.uaa.security.AuthJwtProperties;
import com.chumbok.uaa.security.TokenScopes;
import com.chumbok.uaa.util.DateUtility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class JwtTokenFactory {

    private final AuthJwtProperties authJwtProperties;
    private final DateUtility dateUtility;

    public JwtTokenFactory(AuthJwtProperties authJwtProperties, DateUtility dateUtility) {
        this.authJwtProperties = authJwtProperties;
        this.dateUtility = dateUtility;
    }

    /**
     * Factory method for issuing new JWT Tokens.
     */
    public AccessJwtToken createAccessToken(String username, List<GrantedAuthority> authorities) {

        if (StringUtils.isBlank(username))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (authorities == null || authorities.isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("scopes", authorities.stream().map(s -> s.toString()).collect(Collectors.toList()));

        LocalDateTime currentTime = dateUtility.getCurrentDate();
        Date issueDate = Date.from(currentTime.toInstant(ZoneOffset.UTC));
        Date expiration = Date.from(currentTime.plusMinutes(
                authJwtProperties.getTokenExpirationTimeInSecond()).toInstant(ZoneOffset.UTC));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(authJwtProperties.getTokenIssuer())
                .setIssuedAt(issueDate)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, authJwtProperties.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token, claims);
    }

    public JwtToken createRefreshToken(String username) {

        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        LocalDateTime currentTime = dateUtility.getCurrentDate();
        Date issueDate = Date.from(currentTime.toInstant(ZoneOffset.UTC));
        Date expiration = Date.from(currentTime.plusSeconds(
                authJwtProperties.getRefreshTokenExpirationTimeInSecond()).toInstant(ZoneOffset.UTC));

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("scopes", Arrays.asList(TokenScopes.REFRESH_TOKEN.authority()));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(authJwtProperties.getTokenIssuer())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(issueDate)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, authJwtProperties.getTokenSigningKey())
                .compact();


        return new AccessJwtToken(token, claims);
    }
}
