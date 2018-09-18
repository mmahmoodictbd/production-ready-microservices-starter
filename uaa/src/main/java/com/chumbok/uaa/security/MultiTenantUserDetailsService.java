package com.chumbok.uaa.security;

import com.chumbok.uaa.domain.model.User;
import com.chumbok.uaa.domain.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserDetailsService to load UserDetails based on org, tenant and username.
 *
 */
@Service("userDetailsService")
public class MultiTenantUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public MultiTenantUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * In this method, username parameter split into org, tenant and username, use those to search in userRepository.
     * UsernamePasswordAuthenticationToken is created in com.chumbok.uaa.controller.LoginController merged
     * org, tenant and username into UsernamePasswordAuthenticationToken's username.
     * @param username is combination of org, tenant and username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String[] orgTenantUsername = username.split(String.valueOf(Character.LINE_SEPARATOR));

        if (orgTenantUsername == null || orgTenantUsername.length != 3) {
            throw new UsernameNotFoundException("Org, Tenant and Username must be provided.");
        }

        User user = userRepository.find(orgTenantUsername[0], orgTenantUsername[1], orgTenantUsername[2]);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username[%s] not found for Org[%s] Tenant[%s].",
                    orgTenantUsername[2], orgTenantUsername[0], orgTenantUsername[1]));
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getRole()))
                .collect(Collectors.toList());

        String authPrinciple = user.getOrg().getOrg()
                + String.valueOf(Character.LINE_SEPARATOR)
                + user.getTenant().getTenant()
                + String.valueOf(Character.LINE_SEPARATOR)
                + user.getUsername();

        return new org.springframework.security.core.userdetails.User(authPrinciple, user.getPassword(), user.isEnabled(),
                true, true, true, authorities);
    }
}
