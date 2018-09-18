package com.chumbok.uaa.security;

import com.chumbok.uaa.domain.model.Org;
import com.chumbok.uaa.domain.model.Role;
import com.chumbok.uaa.domain.model.Tenant;
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

public class MultiTenantUserDetailsServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UserRepository userRepositoryMock;
    private final MultiTenantUserDetailsService userDetailsService;

    public MultiTenantUserDetailsServiceTest() {
        this.userRepositoryMock = mock(UserRepository.class);
        this.userDetailsService = new MultiTenantUserDetailsService(userRepositoryMock);
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenProvidedUsernameDoesNotContainOrgOrTenantOrUsername() {

        // Given
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Org, Tenant and Username must be provided.");

        // When
        userDetailsService.loadUserByUsername("MyWithoutOrgTenantUsername");

        // Then
        // // Expect test to be passed.
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotFoundInDB() {

        // Given
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Username[MyUsername] not found for Org[MyOrg] Tenant[MyTenant].");

        // When
        String providedUsername = "MyOrg"
                + String.valueOf(Character.LINE_SEPARATOR)
                + "MyTenant"
                + String.valueOf(Character.LINE_SEPARATOR)
                + "MyUsername";
        userDetailsService.loadUserByUsername(providedUsername);

        // Then
        // // Expect test to be passed.
    }

    @Test
    public void shouldReturnUserDetailsWhenUserFoundInDB() {

        // Given

        Role role = new Role();
        role.setRole("HELLO");
        User userFromDB = new User();
        userFromDB.setOrg(new Org("MyOrg"));
        userFromDB.setTenant(new Tenant("MyTenant"));
        userFromDB.setUsername("MyUsername");
        userFromDB.setPassword("MyPass");
        userFromDB.setDisplayName("MyName");
        userFromDB.setRoles(Collections.singleton(role));
        userFromDB.setEnabled(true);

        when(userRepositoryMock.find(anyString(), anyString(), anyString())).thenReturn(userFromDB);

        // When
        String providedUsername = "MyOrg"
                + String.valueOf(Character.LINE_SEPARATOR)
                + "MyTenant"
                + String.valueOf(Character.LINE_SEPARATOR)
                + "MyUsername";
        UserDetails userDetails = userDetailsService.loadUserByUsername(providedUsername);

        // Then
        assertEquals("MyOrg"
                        + String.valueOf(Character.LINE_SEPARATOR) + "MyTenant"
                        + String.valueOf(Character.LINE_SEPARATOR) + "MyUsername",
                userDetails.getUsername());
        assertEquals("MyPass", userDetails.getPassword());
        assertEquals("[ROLE_HELLO]", userDetails.getAuthorities().toString());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

}