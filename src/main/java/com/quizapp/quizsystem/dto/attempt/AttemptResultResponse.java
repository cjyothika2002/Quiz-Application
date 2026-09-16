package com.quizapp.quizsystem.dto.attempt;

import com.quizapp.quizsystem.entity.AttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttemptResultResponse {
    private Long attemptId;
    private String quizTitle;
    private int totalQuestions;
    private int answered;
    private int correct;
    private int wrong;
    private int unanswered;
    private int score;
    private double percentage;
    private long timeTakenSeconds;
    private AttemptStatus status;
}
