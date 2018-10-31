package com.chumbok.uaa.dto.response;

import lombok.Data;

@Data
public class LoggedInUserInfoResponse {

    private String username;
    private String displayName;
    private String profilePicture28x28pxUrl;
    private String profilePictureUrl;
    private String selectedTheme;
    private String selectedLanguage;
}
