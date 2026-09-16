package com.quizapp.quizsystem.dto.attempt;

import com.quizapp.quizsystem.entity.AttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttemptHistoryResponse {
    private Long attemptId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private AttemptStatus status;
    private LocalDateTime startTime;
    private LocalDateTime submittedAt; // null if never finalized normally
}
