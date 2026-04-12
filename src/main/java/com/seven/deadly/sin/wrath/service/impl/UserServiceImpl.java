package com.seven.deadly.sin.wrath.service.impl;

import com.seven.deadly.sin.wrath.dto.common.PageResponseDTO;
import com.seven.deadly.sin.wrath.dto.common.enums.Status;
import com.seven.deadly.sin.wrath.dto.request.UserDTO;
import com.seven.deadly.sin.wrath.dto.response.UserResultDTO;
import com.seven.deadly.sin.wrath.entity.User;
import com.seven.deadly.sin.wrath.exception.ResourceExistException;
import com.seven.deadly.sin.wrath.exception.ResourceNotFoundException;
import com.seven.deadly.sin.wrath.repository.UserRepository;
import com.seven.deadly.sin.wrath.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "users",
            key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':sort:' + #pageable.sort.toString().replace(' ', '')"
    )
    @Override
    public PageResponseDTO<UserResultDTO> getAllUser(Pageable pageable) {

        Page<User> users = userRepository.findAllByStatus(Status.ACTIVE, pageable);

        List<UserResultDTO> usersContent = users.getContent()
                                                .stream()
                                                .map(UserServiceImpl::buildUserDTO)
                                                .toList();

        return PageResponseDTO.<UserResultDTO>builder()
                            .content(usersContent)
                            .page(users.getNumber())
                            .size(users.getSize())
                            .totalPages(users.getTotalPages())
                            .totalElements(users.getTotalElements())
                            .build();
    }


    @Cacheable(value = "user", key = "#id")
    @Override
    public UserResultDTO getUserById(String id) {

        User user = getUserByUserId(id).orElseThrow(() ->
                new ResourceNotFoundException("user not found.")
        );

        return buildUserDTO(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public String saveUser(UserDTO request) {

        getUserByUsername(request.getUsername()).ifPresent(u -> {
            throw new ResourceExistException(
                    String.format("username '%s' already exist.", u.getUsername())
            );
        });

        final String id = UUID.randomUUID().toString();

        userRepository.save(User.builder()
                                .userId(id)
                                .username(request.getUsername())
                                .name(request.getName())
                                .alias(request.getAlias())
                                .age(request.getAge())
                                .status(Status.ACTIVE)
                                .build());

        return id;
    }

    @CachePut(value = "user", key = "#id")
    @CacheEvict(value = "users", allEntries = true)
    @Override
    public UserResultDTO putUser(String id, UserDTO request) {

        User currentUser = getUserByUserId(id).orElseThrow(() ->
                new ResourceNotFoundException("user not found.")
        );

        currentUser.setName(request.getName());
        currentUser.setAge(request.getAge());
        currentUser.setAlias(request.getAlias());

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

    public Optional<User> getUserByUserId(String id) {
        return userRepository.findUserByUserId(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    private static UserResultDTO buildUserDTO(User user) {
        return UserResultDTO.builder()
                            .id(user.getUserId())
                            .name(user.getName())
                            .age(user.getAge())
                            .username(user.getUsername())
                            .alias(user.getAlias())
                            .status(user.getStatus())
                            .createdBy(user.getCreatedBy())
                            .createdDate(user.getCreatedDate())
                            .updatedBy(user.getUpdatedBy())
                            .updatedDate(user.getUpdatedDate())
                            .build();
    }
}
