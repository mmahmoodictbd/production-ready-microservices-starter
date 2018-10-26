package com.chumbok.uaa.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class VersionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new VersionController(environment)).build();
    }

    @Test
    public void shouldReturnArtifactVersionWhenDefinedInApplicationYaml() throws Exception {

        mockMvc.perform(get("/version"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andDo(print());
    }

    @Configuration
    static class Config {

        @Bean
        public Environment environment() {
            Environment mockEnv = mock(StandardEnvironment.class);
            when(mockEnv.getProperty("info.build.version", "NOT_DEFINED")).thenReturn("1.0.0");
            return mockEnv;
        }
    }
}