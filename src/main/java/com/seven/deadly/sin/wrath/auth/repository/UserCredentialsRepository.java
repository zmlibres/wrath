package com.seven.deadly.sin.wrath.auth.repository;

import com.seven.deadly.sin.wrath.user.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredential, String> {
    Optional<UserCredential> findByUsernameOrEmail(String username, String email);
}
