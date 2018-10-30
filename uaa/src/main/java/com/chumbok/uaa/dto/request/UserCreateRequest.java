package com.chumbok.uaa.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserCreateRequest extends UserUpdateRequest {

    @NotNull
    @Size(min = 2, max = 255)
    private String username;

    @NotNull
    @Size(min = 5, max = 255)
    private String password;

}
