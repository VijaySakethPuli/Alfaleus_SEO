package com.alfaleus.cms;

import com.alfaleus.cms.domain.Role;
import com.alfaleus.cms.domain.User;
import com.alfaleus.cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@alfaleus.com");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            admin.setBio("System Administrator");
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@alfaleus.com / password");
        }
    }
}
