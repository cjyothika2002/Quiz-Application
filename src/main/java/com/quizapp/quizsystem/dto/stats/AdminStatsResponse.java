package com.quizapp.quizsystem.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalCategories;
    private long totalQuizzes;
    private long totalQuestions;
    private long totalAttempts;
    private long completedAttempts;
    private long abandonedAttempts;
    private double averageScorePercentage;
}
