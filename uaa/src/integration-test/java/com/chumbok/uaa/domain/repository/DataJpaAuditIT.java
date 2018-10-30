package com.chumbok.uaa.domain.repository;

import com.chumbok.security.util.SecurityUtil;
import com.chumbok.security.util.SecurityUtil.AuthenticatedUser;
import com.chumbok.uaa.Application;
import com.chumbok.uaa.domain.model.Org;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class, DataJpaAuditIT.Config.class})
@ActiveProfiles("it")
public class DataJpaAuditIT {

    @Autowired
    private OrgRepository orgRepository;

    @Test
    public void testIfAuditFieldsPopulated() {

        // Given

        Org org = new Org();
        org.setId("uuid");
        org.setOrg("Org");

        // When
        Org persistedOrg = orgRepository.save(org);

        // Then
        assertNotNull(persistedOrg.getCreatedBy());
        assertNotNull(persistedOrg.getCreatedAt());
        assertNotNull(persistedOrg.getUpdatedBy());
        assertNotNull(persistedOrg.getUpdatedAt());
    }

    @Configuration
    static class Config {

        @Bean
        public SecurityUtil securityUtil() {

            SecurityUtil securityUtilMock = mock(SecurityUtil.class);
            AuthenticatedUser authenticatedUser
                    = new AuthenticatedUser("MyOrg", "MyTenant", "MyUsername", Collections.emptyList());
            when(securityUtilMock.getAuthenticatedUser()).thenReturn(Optional.of(authenticatedUser));

            return securityUtilMock;
        }

    }

}
