package com.chumbok.uaa.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Data class to read properties from application yaml.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.chumbok.uaa.jwt")
public class AuthJwtProperties {

    /**
     * Auth token to be expired in seconds.
     */
    private Integer tokenExpirationTimeInSecond;

    /**
     * Auth refresh token to be expired in seconds.
     * NOT using currently.
     */
    private Integer refreshTokenExpirationTimeInSecond;

    /**
     * Auth token issuer.
     */
    private String tokenIssuer;

    /**
     * Auth token signing private key path.
     */
    private String tokenSigningPrivateKeyPath;

}
