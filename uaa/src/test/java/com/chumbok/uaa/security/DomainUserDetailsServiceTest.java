package com.chumbok.uaa.security;

import com.chumbok.uaa.domain.model.Role;
import com.chumbok.uaa.domain.model.User;
import com.chumbok.uaa.domain.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DomainUserDetailsServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UserRepository userRepositoryMock;
    private final DomainUserDetailsService domainUserDetailsService;

    public DomainUserDetailsServiceTest() {
        this.userRepositoryMock = mock(UserRepository.class);
        this.domainUserDetailsService = new DomainUserDetailsService(userRepositoryMock);
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenProvidedUsernameDoesNotContainBothDomainAndUsername() {

        // Given
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Domain and Username must be provided.");

        // When
        domainUserDetailsService.loadUserByUsername("MyWithoutDomainUsername");

        // Then
        // // Expect test to be passed.
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotFoundInDB() {

        // Given
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Username[MyEmail] not found for domain[MyDomain].");

        // When
        String providedUsername = "MyDomain" + String.valueOf(Character.LINE_SEPARATOR) + "MyEmail";
        domainUserDetailsService.loadUserByUsername(providedUsername);

        // Then
        // // Expect test to be passed.
    }

    @Test
    public void shouldReturnUserDetailsWhenUserFoundInDB() {

        // Given

        Role role = new Role();
        role.setRole("HELLO");
        User userFromDB = new User();
        userFromDB.setDomain("MyDomain");
        userFromDB.setEmail("MyEmail");
        userFromDB.setPassword("MyPass");
        userFromDB.setDisplayName("MyName");
        userFromDB.setRoles(Collections.singleton(role));
        userFromDB.setEnabled(true);

        when(userRepositoryMock.findByDomainAndEmail(anyString(), anyString())).thenReturn(userFromDB);

        // When
        String providedUsername = "MyDomain" + String.valueOf(Character.LINE_SEPARATOR) + "MyEmail";
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(providedUsername);

        // Then
        assertEquals("MyDomain" + String.valueOf(Character.LINE_SEPARATOR) + "MyEmail", userDetails.getUsername());
        assertEquals("MyPass", userDetails.getPassword());
        assertEquals("[ROLE_HELLO]", userDetails.getAuthorities().toString());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

}