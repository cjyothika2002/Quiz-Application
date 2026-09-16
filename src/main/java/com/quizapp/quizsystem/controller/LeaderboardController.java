package com.quizapp.quizsystem.controller;

import com.quizapp.quizsystem.dto.leaderboard.LeaderboardEntry;
import com.quizapp.quizsystem.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/{quizId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(@PathVariable Long quizId) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(quizId));
    }
}
