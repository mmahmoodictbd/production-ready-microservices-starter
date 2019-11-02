package com.chumbok.uaa.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PublicUserCreateRequest {

    @NotNull
    @Size(min = 2, max = 255)
    private String org;

    @NotNull
    @Size(min = 2, max = 255)
    private String tenant;

    @NotNull
    @Size(min = 2, max = 255)
    private String username;

    @NotNull
    @Size(min = 5, max = 255)
    private String password;

    private Set<String> roles = new HashSet<>();

    private boolean enabled;
}
