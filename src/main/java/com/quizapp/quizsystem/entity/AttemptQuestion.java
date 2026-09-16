package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Freezes exactly which Questions were selected for a given Attempt, and in
 * what order each question's Options should be displayed (Sections 9/10/17).
 *
 * Created once when the attempt starts. This is what makes a page refresh
 * safe (Section 43): the service looks up existing AttemptQuestion rows for
 * the active attempt instead of drawing a brand-new random sample.
 */
@Entity
@Table(name = "attempt_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttemptQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * The order in which this question's option IDs should be displayed for
     * THIS attempt, stored as a comma-separated list of Option ids, e.g.
     * "37,35,38,36". Frozen at attempt-start time so a refresh shows the
     * same shuffled order rather than re-shuffling (Section 10).
     */
    @Column(nullable = false, length = 500)
    private String optionOrder;

    /**
     * 0-based position of this question within the attempt (question 1, 2, 3...).
     * Lets the frontend request "next/previous question" predictably.
     */
    @Column(nullable = false)
    private int position;
}
