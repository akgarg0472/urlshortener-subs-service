package com.akgarg.subsservice.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class InMemoryUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return switch (username) {
            case "admin" -> new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            case "user" -> new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
            default -> throw new UsernameNotFoundException("no user found with username: " + username);
        };
    }

}
