package com.seven.deadly.sin.wrath.user.controller;

import com.seven.deadly.sin.wrath.common.dto.PageResponse;
import com.seven.deadly.sin.wrath.security.service.CustomUserDetails;
import com.seven.deadly.sin.wrath.user.dto.request.UserRequest;
import com.seven.deadly.sin.wrath.user.dto.response.UserResponse;
import com.seven.deadly.sin.wrath.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUser(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUser(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") String id) {

        UserResponse result = userService.getUserById(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody UserRequest request,
                                      Authentication authentication) throws URISyntaxException {
        String id = userService.saveUser(request, authentication);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                             .path("/{id}")
                                             .buildAndExpand(id)
                                             .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") String id,
                                                   @RequestBody UserRequest request,
                                                   Authentication authentication) {

        return new ResponseEntity<>(userService.putUser(id, request, authentication), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {

        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
