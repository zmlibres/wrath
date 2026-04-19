package com.seven.deadly.sin.wrath.user.service.impl;

import com.seven.deadly.sin.wrath.common.dto.PageResponse;
import com.seven.deadly.sin.wrath.common.enums.Role;
import com.seven.deadly.sin.wrath.common.enums.Status;
import com.seven.deadly.sin.wrath.user.dto.request.UserRequest;
import com.seven.deadly.sin.wrath.user.dto.response.UserResponse;
import com.seven.deadly.sin.wrath.user.entity.User;
import com.seven.deadly.sin.wrath.common.exception.ResourceExistException;
import com.seven.deadly.sin.wrath.common.exception.ResourceNotFoundException;
import com.seven.deadly.sin.wrath.user.repository.UserRepository;
import com.seven.deadly.sin.wrath.user.entity.UserCredential;
import com.seven.deadly.sin.wrath.auth.repository.UserCredentialsRepository;
import com.seven.deadly.sin.wrath.security.service.CustomUserDetailsService;
import com.seven.deadly.sin.wrath.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CustomUserDetailsService customUserDetailsService;

    private final UserCredentialsRepository userCredentialsRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserCredentialsRepository userCredentialsRepository,
                           CustomUserDetailsService customUserDetailsService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Cacheable(value = "users",
            key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':sort:' + #pageable.sort.toString().replace(' ', '')"
    )
    @Override
    public PageResponse<UserResponse> getAllUser(Pageable pageable) {

        Page<User> users = userRepository.findAllByStatus(Status.ACTIVE, pageable);

        List<UserResponse> usersContent = users.getContent()
                                               .stream()
                                               .map(UserServiceImpl::buildUserDTO)
                                               .toList();

        return PageResponse.<UserResponse>builder()
                           .content(usersContent)
                           .page(users.getNumber())
                           .size(users.getSize())
                           .totalPages(users.getTotalPages())
                           .totalElements(users.getTotalElements())
                           .build();
    }


    @Cacheable(value = "user", key = "#id")
    @Override
    public UserResponse getUserById(String id) {

        User user = getUserByUserId(id).orElseThrow(() ->
                new ResourceNotFoundException("user not found.")
        );

        return buildUserDTO(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    @Override
    public String saveUser(UserRequest request, Authentication authentication) {
        try {
            Optional<UserCredential> userDetails = userCredentialsRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail());

            if (userDetails.isPresent()) {
                throw new ResourceExistException("User already exist.");

            }

            final String userId = UUID.randomUUID().toString();

            User user = User.builder()
                            .userId(userId)
                            .name(request.getName())
                            .alias(request.getAlias())
                            .age(request.getAge())
                            .status(Status.ACTIVE)
                            .createdBy(authentication.getName())
                            .build();

            userRepository.save(user);

            String hashedPassword = passwordEncoder.encode(request.getPassword());

            userCredentialsRepository.save(UserCredential.builder()
                                                         .user(user)
                                                         .username(request.getUsername())
                                                         .email(request.getEmail())
                                                         .password(hashedPassword)
                                                         .role(Role.USER.toString())
                                                         .enabled(true)
                                                         .build());

            return userId;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @CachePut(value = "user", key = "#id")
    @CacheEvict(value = "users", allEntries = true)
    @Override
    public UserResponse putUser(String id, UserRequest request,
                                Authentication authentication) {

        User currentUser = getUserByUserId(id).orElseThrow(() ->
                new ResourceNotFoundException("user not found.")
        );

        currentUser.setName(request.getName());
        currentUser.setAge(request.getAge());
        currentUser.setAlias(request.getAlias());
        currentUser.setUpdatedBy(authentication.getName());

        User user = userRepository.save(currentUser);

        return buildUserDTO(user);
    }

    @CacheEvict(value = {"user", "users"}, allEntries = true)
    @Override
    public void deleteUser(String id) {
        User user = getUserByUserId(id).orElseThrow(() ->
                new ResourceNotFoundException("user not found.")
        );

        user.setStatus(Status.INACTIVE);

        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByUserId(String id) {
        return userRepository.findUserByUserId(id);
    }

    private static UserResponse buildUserDTO(User user) {
        return UserResponse.builder()
                           .id(user.getUserId())
                           .name(user.getName())
                           .age(user.getAge())
                           .alias(user.getAlias())
                           .status(user.getStatus())
                           .createdBy(user.getCreatedBy())
                           .createdDate(user.getCreatedDate())
                           .updatedBy(user.getUpdatedBy())
                           .updatedDate(user.getUpdatedDate())
                           .build();
    }
}
