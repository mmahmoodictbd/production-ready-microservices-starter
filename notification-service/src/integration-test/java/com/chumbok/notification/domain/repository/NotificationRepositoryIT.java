package com.chumbok.notification.domain.repository;


import com.chumbok.notification.Application;
import com.chumbok.notification.domain.model.Notification;
import com.chumbok.notification.domain.model.NotificationType;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.security.util.SecurityUtil.AuthenticatedUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
@SpringBootTest(classes = {Application.class, NotificationRepositoryIT.Config.class})
@ActiveProfiles("it")
public class NotificationRepositoryIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private NotificationRepository notificationRepository;

    @After
    public void tearDown() {
        notificationRepository.deleteAll();
    }

    @Test
    public void shouldAbleToSave() {

        // Given
        Notification notification = new Notification();
        notification.setId("uuid1");
        notification.setType(NotificationType.CRITICAL);
        notification.setContent("System is down!");

        // When
        notification = notificationRepository.saveAndFlush(notification);

        // Then
        assertNotNull(notification.getCreatedAt());
        assertNotNull(notification.getCreatedAt());
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
