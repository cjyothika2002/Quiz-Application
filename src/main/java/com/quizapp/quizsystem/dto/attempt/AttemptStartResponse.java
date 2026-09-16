package com.quizapp.quizsystem.dto.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AttemptStartResponse {
    private Long attemptId;
    private String quizTitle;
    private int totalQuestions;
    private LocalDateTime startTime;
    private LocalDateTime expiryTime;
    private long secondsRemaining;
    private List<QuestionAttemptResponse> questions;
}
