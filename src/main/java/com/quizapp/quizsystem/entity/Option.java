package com.quizapp.quizsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One multiple-choice option for a Question (Section 10).
 *
 * CRITICAL SECURITY NOTE (Section 11):
 * The `correct` field must NEVER be serialized into a response sent to a
 * user while they are attempting a quiz. The quiz-taking DTOs deliberately
 * omit this field entirely — see dto.OptionResponseDto — so there is no way
 * to accidentally leak it through a forgotten @JsonIgnore.
 */
@Entity
@Table(name = "options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @NotBlank
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean correct;
}
