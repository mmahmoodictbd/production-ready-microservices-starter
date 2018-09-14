package com.chumbok.uaa.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.util.Date;

/**
 * JWT utils
 */
public class JwtUtil {

    /**
     * Return URL safe compact JWT string.
     *
     * @param claims
     * @param tokenIssuer
     * @param issueDate
     * @param expirationDate
     * @param privateKey
     * @return
     */
    public String getJwts(Claims claims, String tokenIssuer, Date issueDate, Date expirationDate, PrivateKey privateKey) {
        return Jwts.builder().setClaims(claims)
                .setIssuer(tokenIssuer)
                .setIssuedAt(issueDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

}
