package com.chumbok.uaa.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.chumbok.uaa.jwt")
public class AuthJwtProperties {

    private Integer tokenExpirationTimeInSecond;
    private Integer refreshTokenExpirationTimeInSecond;
    private String tokenIssuer;
    private String tokenSigningKey;

}
