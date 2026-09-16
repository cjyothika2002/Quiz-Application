package com.quizapp.quizsystem.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Admin-facing view of a quiz: includes management fields like published status and bank size. */
@Getter
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private int questionsPerAttempt;
    private long questionBankSize;
    private boolean published;
}
