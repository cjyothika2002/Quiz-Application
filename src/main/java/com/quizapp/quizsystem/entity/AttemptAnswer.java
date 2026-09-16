package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Records the user's selected Option for one Question within one Attempt
 * (Section 18). One row per (attempt, question) pair, upserted every time
 * the user answers or changes an answer while ACTIVE.
 *
 * `selectedOption` is nullable — null means "presented but left unanswered",
 * which is what lets the backend distinguish unanswered from answered when
 * calculating unansweredCount at submission time (Section 22).
 */
@Entity
@Table(
        name = "attempt_answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"attempt_id", "question_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** Null = left unanswered. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;

    private LocalDateTime answeredAt;
}
