package com.quizapp.quizsystem.exception;

/**
 * Thrown for state-conflict situations specific to the quiz engine:
 * submitting an already-COMPLETED attempt, answering an EXPIRED attempt,
 * etc. (Sections 21, 37). Maps to HTTP 409 Conflict — distinct from a plain
 * 400, because the request itself is valid, it's just too late/out of order.
 */
public class AttemptStateException extends RuntimeException {
    public AttemptStateException(String message) {
        super(message);
    }
}
