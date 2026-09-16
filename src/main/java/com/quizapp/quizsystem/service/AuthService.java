package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.auth.AuthResponse;
import com.quizapp.quizsystem.dto.auth.LoginRequest;
import com.quizapp.quizsystem.dto.auth.RegisterRequest;
import com.quizapp.quizsystem.entity.User;
import com.quizapp.quizsystem.security.JwtUtil;
import com.quizapp.quizsystem.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /** Section 5: register, then immediately log in so the client gets a token right away. */
    public AuthResponse register(RegisterRequest request) {
        User user = userService.register(request);
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtil.generateToken(principal);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    /**
     * Section 4/5/32: delegates credential checking to Spring Security's
     * AuthenticationManager (which uses CustomUserDetailsService + the
     * BCryptPasswordEncoder bean under the hood) rather than comparing
     * passwords by hand here.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userService.findByEmail(request.getEmail());
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtil.generateToken(principal);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
