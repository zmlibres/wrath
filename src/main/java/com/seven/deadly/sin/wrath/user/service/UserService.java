package com.seven.deadly.sin.wrath.user.service;

import com.seven.deadly.sin.wrath.common.dto.PageResponse;
import com.seven.deadly.sin.wrath.security.service.CustomUserDetails;
import com.seven.deadly.sin.wrath.user.dto.request.UserRequest;
import com.seven.deadly.sin.wrath.user.dto.response.UserResponse;
import com.seven.deadly.sin.wrath.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface UserService {

    UserResponse getUserById(String id);

    String saveUser(UserRequest request, Authentication authentication);

    PageResponse<UserResponse> getAllUser(Pageable pageable);

    UserResponse putUser(String id, UserRequest request, Authentication authentication);

    void deleteUser(String id);

    Optional<User> getUserByUserId(String id);

}
