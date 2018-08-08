package com.chumbok.uaa.security.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class AccessJwtToken implements JwtToken {

    private final String token;

    @JsonIgnore
    private Claims claims;

}
