package com.chumbok.uaa.security;


public enum TokenScopes {

    REFRESH_TOKEN;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
