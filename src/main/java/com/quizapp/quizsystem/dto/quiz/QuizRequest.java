package com.quizapp.quizsystem.dto.quiz;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @Min(value = 1, message = "Questions per attempt must be at least 1")
    private int questionsPerAttempt;
}
