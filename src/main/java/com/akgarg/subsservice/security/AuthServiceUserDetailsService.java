package com.akgarg.subsservice.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Profile("prod")
public class AuthServiceUserDetailsService implements UserDetailsService {

    private final RestClient restClient;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            final var response = restClient
                    .post()
                    .uri("auth-service/api/v1/check-admin")
                    .body(Map.of("userId", username))
                    .retrieve()
                    .toEntity(Map.class)
                    .getBody();

            if (response == null || response.isEmpty()) {
                throw new IllegalStateException("Invalid response received from auth service");
            }

            final boolean success = Boolean.parseBoolean(String.valueOf(response.get("success")));

            if (success) {
                return new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            } else {
                return new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("No admin user found with userId=%s".formatted(username));
        }
    }

}
