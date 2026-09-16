package com.quizapp.quizsystem.dto.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProgressResponse {
    private long totalAttempts;
    private long completedAttempts;
    private long abandonedAttempts;
    private double bestScorePercentage;
    private double averageScorePercentage;
    private double latestScorePercentage; // 0 if no attempts yet
}
