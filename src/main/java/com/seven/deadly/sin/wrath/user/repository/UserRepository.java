package com.seven.deadly.sin.wrath.user.repository;

import com.seven.deadly.sin.wrath.common.enums.Status;
import com.seven.deadly.sin.wrath.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findUserByUserId(String userId);

    Page<User> findAllByStatus(Status status, Pageable pageable);

}
