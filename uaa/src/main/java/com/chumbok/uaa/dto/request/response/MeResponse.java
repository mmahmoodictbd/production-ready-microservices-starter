package com.chumbok.uaa.dto.request.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeResponse {

    private String username;
    private String displayName;
    private String profilePicture28x28pxUrl;
    private String profilePictureUrl;
    private String selectedTheme;
    private String selectedLanguage;
}
