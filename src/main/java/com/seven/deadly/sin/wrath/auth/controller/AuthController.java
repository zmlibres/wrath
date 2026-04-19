package com.seven.deadly.sin.wrath.auth.controller;

import com.seven.deadly.sin.wrath.auth.dto.request.LoginRequest;
import com.seven.deadly.sin.wrath.auth.dto.response.LoginResponse;
import com.seven.deadly.sin.wrath.auth.dto.request.RefreshTokenRequest;
import com.seven.deadly.sin.wrath.auth.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public static final String ACCESS_TOKEN = "accessToken";

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("Login");
        LoginResponse response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'USER')")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {

        String newAccessToken = authenticationService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(Map.of(ACCESS_TOKEN, newAccessToken));

    }
}
