package com.seven.deadly.sin.wrath.auth.service.impl;

import com.seven.deadly.sin.wrath.auth.dto.request.LoginRequest;
import com.seven.deadly.sin.wrath.auth.dto.response.LoginResponse;
import com.seven.deadly.sin.wrath.common.exception.UnauthorizedException;
import com.seven.deadly.sin.wrath.security.service.CustomUserDetails;
import com.seven.deadly.sin.wrath.security.jwt.JwtService;
import com.seven.deadly.sin.wrath.auth.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final long ACCESS_EXPIRATION = 1000 * 60 * 15; // 15 mins
    public static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    private final JwtService jwtServiceImpl;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;



    public AuthenticationServiceImpl(JwtService jwtServiceImpl,
                                     AuthenticationManager authenticationManager,
                                     UserDetailsService userDetailsService,
                                     PasswordEncoder passwordEncoder) {
        this.jwtServiceImpl = jwtServiceImpl;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        try {
            // authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            System.out.println("Authentication: {}" + authentication);

            // get authenticated user
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

            // generate access token
            String accessToken = jwtServiceImpl.generateAccessToken(user);
            // generate refresh token
            String refreshToken = jwtServiceImpl.generateRefreshToken(user);

            return LoginResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .accessExpiresIn(ACCESS_EXPIRATION)
                                .refreshExpiresIn(REFRESH_EXPIRATION)
                                .build();
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }

    }

    @Transactional
    @Override
    public String signUp(LoginRequest request) {
        return "";
    }

    @Override
    public String refreshToken(String refreshToken) {

        // validate refresh token
        if (!jwtServiceImpl.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // extract claims
        Claims claims = jwtServiceImpl.extractAllClaims(refreshToken);

        // get username
        String username = claims.getSubject();

        // load user
        CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        // generate new access token
        String newAccessToken = jwtServiceImpl.generateAccessToken(user);

        return newAccessToken;
    }
}
