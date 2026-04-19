package com.seven.deadly.sin.wrath.security.service;

import com.seven.deadly.sin.wrath.common.enums.Status;
import com.seven.deadly.sin.wrath.user.entity.UserCredential;
import com.seven.deadly.sin.wrath.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    private final UserCredential userCredential;

    public CustomUserDetails(User user, UserCredential userCredential) {
        this.user = user;
        this.userCredential = userCredential;
    }

    public String getUserId() {
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return userCredential.getUsername();
    }

    @Override
    public String getPassword() {
        return userCredential.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userCredential.getRole().toString()));
    }

    @Override
    public boolean isEnabled() {
        return userCredential.isEnabled() && user.getStatus() == Status.ACTIVE;
    }
}
