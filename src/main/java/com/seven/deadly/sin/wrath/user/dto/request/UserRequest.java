package com.seven.deadly.sin.wrath.user.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String name;
    private int age;
    private String username;
    private String email;
    private String password;
    private String alias;
}
