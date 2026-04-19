package com.seven.deadly.sin.wrath.security.service;

import com.seven.deadly.sin.wrath.user.entity.UserCredential;
import com.seven.deadly.sin.wrath.user.entity.User;
import com.seven.deadly.sin.wrath.auth.repository.UserCredentialsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialsRepository userCredentialsRepository;

    public CustomUserDetailsService(UserCredentialsRepository userCredentialsRepository) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        UserCredential credentials = userCredentialsRepository.findByUsernameOrEmail(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        User user = credentials.getUser();

        return new CustomUserDetails(user, credentials);

    }
}
