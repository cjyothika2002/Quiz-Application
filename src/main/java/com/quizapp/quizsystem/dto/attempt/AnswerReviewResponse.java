package com.quizapp.quizsystem.dto.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerReviewResponse {
    private Long questionId;
    private String questionText;
    private String selectedOptionText; // null if left unanswered
    private String correctOptionText;
    private boolean wasCorrect;
    private String explanation; // may be null
}
