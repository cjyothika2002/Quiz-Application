package com.quizapp.quizsystem.dto.user;

import com.quizapp.quizsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * What the admin's "view registered users" screen receives (Section 4).
 * Deliberately excludes the password hash entirely (Section 38) — there is
 * no field here to accidentally serialize.
 */
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
