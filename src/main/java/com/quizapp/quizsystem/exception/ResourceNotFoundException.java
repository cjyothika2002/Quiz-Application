package com.quizapp.quizsystem.exception;

/** Thrown when a Quiz/Category/Question/Attempt/User id doesn't exist. Maps to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
