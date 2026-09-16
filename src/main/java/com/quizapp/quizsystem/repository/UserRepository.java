package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Used by:
     *  - login (Spring Security's UserDetailsService loads by email)
     *  - registration duplicate-email validation (Section 34)
     */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
