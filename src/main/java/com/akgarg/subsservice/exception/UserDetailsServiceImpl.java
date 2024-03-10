package com.akgarg.subsservice.exception;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RestClient restClient;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        restClient.get().

        throw new UsernameNotFoundException("User is not");
    }

}
