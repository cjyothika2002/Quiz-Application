package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A single question belonging to a Quiz's question bank (Section 10).
 * Correctness lives on Option, not here (see Option.correct).
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String text;

    /**
     * Shown only in the "View Answers" review after submission (Section 24),
     * never during an active attempt.
     */
    @Column(length = 2000)
    private String explanation;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
}
