package com.quizapp.quizsystem.entity;

/**
 * The two application roles (Section 3).
 * Spring Security expects role names, prefixed with "ROLE_", when checking
 * authorities — that prefixing is handled in the security layer, not here.
 */
public enum Role {
    ADMIN,
    USER
}
