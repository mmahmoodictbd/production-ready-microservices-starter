package com.chumbok.uaa.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UserUpdateRequest {

    @Size(min = 2, max = 255)
    private String firstName;

    @Size(min = 2, max = 255)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 255)
    private String displayName;

    private String imageUrl;

    private String timezoneId;

    private String preferredLanguage;

    @NotNull
    @Size(min = 1, max = 50)
    private Set<String> roles;

    private boolean enabled;

}
