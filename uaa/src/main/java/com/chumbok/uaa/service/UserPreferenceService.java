package com.chumbok.uaa.service;

import com.chumbok.security.util.SecurityUtil;
import com.chumbok.uaa.dto.request.response.MeResponse;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceService {

    private SecurityUtil securityUtil;

    public UserPreferenceService(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    public MeResponse getMeInfo() {
        return MeResponse.builder().build();
    }
}
