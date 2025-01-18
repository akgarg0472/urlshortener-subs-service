package com.akgarg.subsservice.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class UsernamePasswordExtractionFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER_NAME = "X-USER-ID";
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @Nonnull final HttpServletRequest request,
            @Nonnull final HttpServletResponse response,
            @Nonnull final FilterChain filterChain
    ) throws ServletException, IOException {
        final var username = request.getHeader(USER_ID_HEADER_NAME);

        if (username != null && !username.isBlank() &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            final var userDetails = userDetailsService.loadUserByUsername(username);
            final var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            final var authenticationDetails = new WebAuthenticationDetailsSource()
                    .buildDetails(request);
            authenticationToken.setDetails(authenticationDetails);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

}
