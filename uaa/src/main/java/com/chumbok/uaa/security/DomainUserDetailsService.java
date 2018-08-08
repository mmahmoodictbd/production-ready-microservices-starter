package com.chumbok.uaa.security;

import com.chumbok.uaa.domain.model.User;
import com.chumbok.uaa.domain.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserDetailsService to load UserDetails based on domain and username.
 *
 */
@Service("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * In this method, username parameter split into domain and username, use those to search in userRepository.
     * @param username is combination of domain and username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String[] domainAndUsername = StringUtils.split(username, String.valueOf(Character.LINE_SEPARATOR));

        if (domainAndUsername == null || domainAndUsername.length != 2) {
            throw new UsernameNotFoundException("Domain and Username must be provided");
        }

        User user = userRepository.findByDomainAndEmail(domainAndUsername[0], domainAndUsername[1]);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username not found for domain, username=%s, domain=%s",
                    domainAndUsername[0], domainAndUsername[1]));
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getRole()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.isEnabled(), true, true, true, authorities);
    }
}
