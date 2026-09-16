package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.auth.RegisterRequest;
import com.quizapp.quizsystem.dto.user.UserResponse;
import com.quizapp.quizsystem.entity.Role;
import com.quizapp.quizsystem.entity.User;
import com.quizapp.quizsystem.exception.BadRequestException;
import com.quizapp.quizsystem.exception.ResourceNotFoundException;
import com.quizapp.quizsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new USER account (Section 5). ADMIN accounts are not
     * self-registered through this public endpoint — they're seeded
     * directly in the DB or created by an existing admin, which keeps the
     * public registration surface from being a path to admin access.
     */
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // Section 31: hash before it ever touches the DB. The plain
                // text value from the request is never persisted or logged.
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /** Admin's "view registered users" (Section 4) — passwords never leave the entity. */
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt()))
                .toList();
    }
}
