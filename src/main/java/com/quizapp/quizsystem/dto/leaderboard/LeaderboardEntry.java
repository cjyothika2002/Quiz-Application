package com.quizapp.quizsystem.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaderboardEntry {
    private int rank;
    private String userName;
    private int bestScore;
    private int totalQuestions;
    private long timeTakenSeconds;
}
