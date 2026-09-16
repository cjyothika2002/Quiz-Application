package com.quizapp.quizsystem.exception;

/**
 * Thrown when a request is well-formed but violates a business rule, e.g.
 * "not enough questions to publish this quiz" (Section 8) or "duplicate
 * email" (Section 34). Maps to HTTP 400.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
