package com.quizapp.quizsystem.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * What a USER sees when browsing quizzes (Section 5). Note there is no
 * `questionBankSize` field — exposing "50 available, 10 will be picked"
 * isn't harmful, but it's also not something the requirements ask users to
 * see, so it's kept off this DTO to match the spec exactly. `timeLimitMinutes`
 * is pre-calculated server-side (Section 13) so the frontend never computes
 * timing itself.
 */
@Getter
@AllArgsConstructor
public class QuizBrowseResponse {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private int questionsPerAttempt;
    private int timeLimitMinutes;
}
