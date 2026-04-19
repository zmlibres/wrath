package com.seven.deadly.sin.wrath.auth.service;

import com.seven.deadly.sin.wrath.auth.dto.request.LoginRequest;
import com.seven.deadly.sin.wrath.auth.dto.response.LoginResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);

    String signUp(@RequestBody LoginRequest request);

    String refreshToken(String refreshToken);
}
