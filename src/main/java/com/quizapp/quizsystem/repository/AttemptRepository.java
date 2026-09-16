package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.Attempt;
import com.quizapp.quizsystem.entity.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    /**
     * THE query behind refresh-safe attempts (Section 43). Before creating a
     * new attempt, the service must check for an existing ACTIVE one for
     * this user+quiz and resume it instead of drawing new random questions.
     *
     * At most one ACTIVE attempt should exist per (user, quiz) at a time —
     * enforced in the service layer, not the DB, since "at most one row
     * matching a status value" isn't a natural SQL constraint.
     */
    Optional<Attempt> findByUserIdAndQuizIdAndStatus(Long userId, Long quizId, AttemptStatus status);

    /** Full attempt history for a user across all quizzes (Section 25). */
    List<Attempt> findByUserIdOrderByStartTimeDesc(Long userId);

    /** History scoped to one quiz (Section 25 example groups by quiz). */
    List<Attempt> findByUserIdAndQuizIdOrderByStartTimeDesc(Long userId, Long quizId);

    /**
     * Raw data source for leaderboard computation (Section 26). Only
     * COMPLETED attempts are fetched — ABANDONED ones are excluded by this
     * query itself, not filtered later, so a service author can't
     * accidentally include them.
     */
    List<Attempt> findByQuizIdAndStatus(Long quizId, AttemptStatus status);

    /**
     * Used by the scheduled expiry sweep (Phase 12): finds attempts that are
     * still marked ACTIVE in the DB but whose expiryTime has already passed
     * — e.g. the user closed the tab before the frontend timer could call
     * auto-submit. These get finalized server-side as ABANDONED or
     * COMPLETED-by-timeout depending on how many answers exist.
     */
    List<Attempt> findByStatusAndExpiryTimeBefore(AttemptStatus status, LocalDateTime cutoff);

    long countByStatus(AttemptStatus status);

    long countByQuizIdAndStatus(Long quizId, AttemptStatus status);

    long countByUserId(Long userId);
}
