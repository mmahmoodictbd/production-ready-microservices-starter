package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.Application;
import com.chumbok.uaa.domain.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("it")
public class DataJpaAuditIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testIfAuditFieldsPopulated() {

        // Given

        User user = new User();
        user.setDomain("Domain");
        user.setEmail("Email");
        user.setPassword("UserPassHash");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setDisplayName("DisplayName");
        user.setTimezoneId("UTC");

        // When
        User persistedUser = userRepository.save(user);

        // Then
        assertNotNull(persistedUser.getCreatedBy());
        assertNotNull(persistedUser.getCreatedAt());
        assertNotNull(persistedUser.getUpdatedBy());
        assertNotNull(persistedUser.getUpdatedAt());
    }

}
