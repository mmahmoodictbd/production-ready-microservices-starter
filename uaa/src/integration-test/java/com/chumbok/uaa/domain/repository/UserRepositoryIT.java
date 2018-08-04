package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.Application;
import com.chumbok.uaa.domain.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("it")
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenSameDomainEmailUserExist() {

        // Given

        expectedException.expect(DataIntegrityViolationException.class);

        User user1 = new User();
        user1.setDomain("MyDomain");
        user1.setEmail("Email");
        user1.setPassword("UserPassHash");
        user1.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setDomain("MyDomain");
        user2.setEmail("Email");
        user2.setPassword("UserPassHash");
        user2.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user2);

        // Then
        // Should just pass because expected exception raised
    }

    @Test
    public void shouldAbleToSaveUserWithDifferentDomainButSameEmail() {

        // Given

        User user1 = new User();
        user1.setDomain("SimpleDomain");
        user1.setEmail("Email");
        user1.setPassword("UserPassHash");
        user1.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setDomain("DifferentDomain");
        user2.setEmail("Email");
        user2.setPassword("UserPassHash");
        user2.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user2);

        // Then

        assertNotNull(user1.getId());
        assertNotNull(user2.getId());
    }

}
