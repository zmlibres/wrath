package com.seven.deadly.sin.wrath.bootstrap;

import com.seven.deadly.sin.wrath.auth.repository.UserCredentialsRepository;
import com.seven.deadly.sin.wrath.common.enums.Role;
import com.seven.deadly.sin.wrath.common.enums.Status;
import com.seven.deadly.sin.wrath.user.entity.User;
import com.seven.deadly.sin.wrath.user.entity.UserCredential;
import com.seven.deadly.sin.wrath.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository,
                       UserCredentialsRepository userCredentialsRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {

        Optional<UserCredential> adminDetail = userCredentialsRepository.findByUsernameOrEmail(
                "admin", "admin@email.com");

        if (adminDetail.isEmpty()) {

            User userAdmin = User.builder()
                                 .userId(UUID.randomUUID().toString())
                                 .age(18)
                                 .alias("iamadmin")
                                 .status(Status.ACTIVE)
                                 .name("super-admin")
                                 .createdBy("super-admin")
                                 .updatedBy("super-admin")
                                 .build();

            userRepository.save(userAdmin);

            String hashedAdminPassword = passwordEncoder.encode("admin");

            userCredentialsRepository.save(UserCredential.builder()
                                     .user(userAdmin)
                                     .username("admin")
                                     .email("admin@email.com")
                                     .password(hashedAdminPassword)
                                     .role(Role.ADMIN.toString())
                                     .enabled(true)
                                     .build());
        }
    }
}
