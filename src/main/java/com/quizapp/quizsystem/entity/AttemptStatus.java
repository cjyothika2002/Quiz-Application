package com.quizapp.quizsystem.entity;

/**
 * The three states an Attempt can be in (Section 20).
 * ACTIVE      - user has started the quiz and the timer has not expired yet.
 * COMPLETED   - user submitted (manually or via auto-submit on timeout).
 * ABANDONED   - attempt expired or was otherwise never properly submitted
 *               by the user; finalized by the backend, not deleted.
 */
public enum AttemptStatus {
    ACTIVE,
    COMPLETED,
    ABANDONED
}
