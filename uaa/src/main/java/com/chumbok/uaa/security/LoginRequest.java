package com.chumbok.uaa.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {

    private String org;
    private String tenant;
    private String username;
    private String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("org") String org,
                        @JsonProperty("tenant") String tenant,
                        @JsonProperty("username") String username,
                        @JsonProperty("password") String password) {
        this.org = org;
        this.tenant = tenant;
        this.username = username;
        this.password = password;
    }

}
