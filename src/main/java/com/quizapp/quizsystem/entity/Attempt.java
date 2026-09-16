package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One quiz-taking session for one user (Sections 17-22).
 *
 * This is the backend's single source of truth for timing, status, and
 * scoring. The frontend timer is cosmetic only — every mutating endpoint
 * (answer, submit) must re-check `expiryTime` against the server clock
 * before doing anything (Section 14/18).
 */
@Entity
@Table(name = "attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * Computed once at attempt creation as
     * startTime + (totalQuestions * secondsPerQuestion). Stored so every
     * later request can check `now > expiryTime` without recomputing
     * anything from client input (Section 14).
     */
    @Column(nullable = false)
    private LocalDateTime expiryTime;

    private LocalDateTime submittedAt;

    /**
     * Snapshot of quiz.questionsPerAttempt at the moment the attempt was
     * created, so later admin edits to the quiz config don't retroactively
     * change how an in-progress or historical attempt is scored/displayed.
     */
    @Column(nullable = false)
    private int totalQuestions;

    private int correctCount;
    private int wrongCount;
    private int unansweredCount;

    /** Score = correctCount, out of totalQuestions (Section 12: no negative marking). */
    private int score;

    @Builder.Default
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptQuestion> attemptQuestions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptAnswer> attemptAnswers = new ArrayList<>();
}
