package com.chumbok.uaa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String displayName;

    private String imageUrl;

    private String timezoneId;

    private String preferredLanguage;

    private Set<String> roles;

    private boolean enabled;
}
