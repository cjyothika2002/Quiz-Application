package com.quizapp.quizsystem.config;

import com.quizapp.quizsystem.entity.Role;
import com.quizapp.quizsystem.entity.User;
import com.quizapp.quizsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * The public /api/auth/register endpoint only ever creates USER accounts
 * (Section 4/5 — admin access shouldn't be self-service). This runs once on
 * startup and creates exactly one ADMIN account if none exists yet, so
 * there's a way in on a fresh database. Credentials come from environment
 * variables — never hard-coded (Section 39) — with local-dev fallbacks.
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@quizapp.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.ADMIN);

        if (!adminExists) {
            User admin = User.builder()
                    .name("Administrator")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Seeded default admin account -> email: " + adminEmail);
        }
    }
}
