package com.seven.deadly.sin.wrath.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private long accessExpiresIn;
    private long refreshExpiresIn;
}
