package com.seven.deadly.sin.wrath.user.dto.response;

import com.seven.deadly.sin.wrath.common.enums.Status;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private int age;
    private String email;
    private String alias;
    private Status status;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
}
