package com.chumbok.uaa.conf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class WebSecurityConfigIT {

    private static final String LOGIN_POST_URL = "/login";

    private static final String LOGIN_BAD_REQUEST_BODY
            = "{ \"username\": \"admin\", \"password\": \"admin\" }";

    private static final String LOGIN_REQUEST_BODY
            = "{ \"org\" : \"Chumbok\", \"tenant\" : \"Chumbok\", \"username\": \"admin\", \"password\": \"admin\" }";

    private static final String LOGIN_WRONG_CRED_REQUEST_BODY
            = "{ \"org\" : \"wrong org\", \"tenant\" : \"Chumbok\", \"username\": \"admin\", \"password\": \"admin\" }";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturn400OnMissingLoginRequestAttributes() throws Exception {

        mockMvc.perform(post(LOGIN_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(LOGIN_BAD_REQUEST_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid request received."))
                .andExpect(cookie().doesNotExist("Authorization"))
                .andDo(print());
    }


    @Test
    public void shouldReturn401OnWrongCredential() throws Exception {

        mockMvc.perform(post(LOGIN_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(LOGIN_WRONG_CRED_REQUEST_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED_REQUEST"))
                .andExpect(jsonPath("$.message").value("Could not authenticate."))
                .andExpect(cookie().doesNotExist("Authorization"))
                .andDo(print());
    }

    @Test
    public void shouldReturnAccessTokenAndAuthorizationCookieWhenSuccessfullyAuthenticate() throws Exception {

        mockMvc.perform(post(LOGIN_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(LOGIN_REQUEST_BODY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(cookie().exists("Authorization"))
                .andExpect(cookie().value("Authorization", notNullValue()));
    }

}
