package com.akgarg.subsservice.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("prod")
@AllArgsConstructor
public class AuthServiceUserDetailsService implements UserDetailsService {

    private final RestClient.Builder authServiceRestClientBuilder;
    private final Environment environment;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.info("Authenticating user {}", username);

        try {
            final var verifyAdminEndpoint = environment.getProperty(
                    "auth.service.endpoints.verify-admin",
                    "/api/v1/verify-admin"
            );

            final var response = authServiceRestClientBuilder.build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(verifyAdminEndpoint)
                            .build())
                    .body(Map.of("user_id", username))
                    .retrieve()
                    .toEntity(Map.class)
                    .getBody();

            if (response == null || response.isEmpty()) {
                throw new IllegalStateException("Invalid response received from auth service");
            }

            final var success = Boolean.parseBoolean(String.valueOf(response.get("success")));

            if (success) {
                return new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            } else {
                return new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("No admin user found with userId: %s".formatted(username));
        }
    }

}