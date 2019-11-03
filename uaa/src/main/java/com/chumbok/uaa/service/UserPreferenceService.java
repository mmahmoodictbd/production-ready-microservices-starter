package com.chumbok.uaa.service;

import com.chumbok.exception.presentation.UnautherizedException;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.security.util.SecurityUtil.AuthenticatedUser;
import com.chumbok.uaa.domain.model.User;
import com.chumbok.uaa.domain.repository.UserRepository;
import com.chumbok.uaa.dto.response.LoggedInUserInfoResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Provide logged in user preferences.
 */
@Service
@AllArgsConstructor
public class UserPreferenceService {

    private static final String PROFILE_PIC_28x28_URL_KEY = "profilePicture28x28Url";
    private static final String PROFILE_PIC_URL_KEY = "profilePictureUrl";
    private static final String SELECTED_THEME_KEY = "selectedTheme";
    private static final String SELECTED_LANG_KEY = "selectedLanguage";

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public LoggedInUserInfoResponse getLoggedInUserInfo() {

        Optional<AuthenticatedUser> authenticatedUserOptional = securityUtil.getAuthenticatedUser();
        if (!authenticatedUserOptional.isPresent()) {
            throw new UnautherizedException("User not loggedIn.");
        }

        AuthenticatedUser authenticatedUser = authenticatedUserOptional.get();

        if (!userRepository.isExist(authenticatedUser.getOrg(), authenticatedUser.getTenant(),
                authenticatedUser.getUsername())) {
            throw new UnautherizedException("User not exist in the system.");
        }

        User user = userRepository.find(authenticatedUser.getOrg(), authenticatedUser.getTenant(),
                authenticatedUser.getUsername());

        LoggedInUserInfoResponse response = new LoggedInUserInfoResponse();
        response.setUsername(user.getUsername());
        response.setDisplayName(user.getDisplayName());
        response.setProfilePicture28x28pxUrl(user.getAdditionalProperties().get(PROFILE_PIC_28x28_URL_KEY));
        response.setProfilePictureUrl(user.getAdditionalProperties().get(PROFILE_PIC_URL_KEY));
        response.setSelectedTheme(user.getAdditionalProperties().get(SELECTED_THEME_KEY));
        response.setSelectedLanguage(user.getAdditionalProperties().get(SELECTED_LANG_KEY));

        return response;
    }
}
