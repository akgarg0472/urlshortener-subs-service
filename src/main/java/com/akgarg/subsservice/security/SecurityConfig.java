package com.akgarg.subsservice.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAuthenticationExceptionHandler authenticationExceptionHandler;
    private final UsernamePasswordExtractionFilter usernamePasswordExtractionFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        final RequestMatcher[] adminOnlyEndpoints = {
                new AntPathRequestMatcher("/api/v1/subscriptions/plans/**", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/api/v1/subscriptions/plans/**", HttpMethod.DELETE.name()),
                new AntPathRequestMatcher("/api/v1/subscriptions/plans/**", HttpMethod.PATCH.name())
        };

        http.authorizeHttpRequests(
                registry -> {
                    for (final RequestMatcher adminOnlyEndpoint : adminOnlyEndpoints) {
                        registry.requestMatchers(adminOnlyEndpoint).hasRole("ADMIN");
                    }

                    registry.anyRequest().permitAll();
                }
        );

        http.csrf(AbstractHttpConfigurer::disable);
        http.userDetailsService(userDetailsService);
        http.addFilterBefore(usernamePasswordExtractionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationExceptionHandler, LogoutFilter.class);
        http.sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint));
        http.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

}
