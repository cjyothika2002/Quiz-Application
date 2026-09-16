package com.quizapp.quizsystem.dto.auth;

import com.quizapp.quizsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Returned after login/registration. Contains the JWT the client must send
 * on subsequent requests (Section 32) — and nothing else sensitive. Note:
 * NO password field exists anywhere on this class (Section 38).
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
