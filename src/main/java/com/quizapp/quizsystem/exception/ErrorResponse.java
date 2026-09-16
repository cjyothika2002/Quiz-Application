package com.quizapp.quizsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Uniform error body for every failed request (Section 35) — never a raw
 * stack trace. Kept intentionally small: status, a human-readable message,
 * and a timestamp.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
