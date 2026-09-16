package com.quizapp.quizsystem.dto.attempt;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * `selectedOptionId` is intentionally NOT @NotNull — sending null is how the
 * frontend represents "clear my previous answer" for this question, and is
 * distinct from never calling this endpoint for a question at all (Section 18).
 */
@Getter
@Setter
public class AnswerRequest {

    @NotNull(message = "questionId is required")
    private Long questionId;

    private Long selectedOptionId;
}
