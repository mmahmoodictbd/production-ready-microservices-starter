package com.chumbok.uaa.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {

    private String domain;
    private String username;
    private String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("domain") String domain,
                        @JsonProperty("username") String username,
                        @JsonProperty("password") String password) {
        this.domain = domain;
        this.username = username;
        this.password = password;
    }

}
