package com.quizapp.quizsystem.controller;

import com.quizapp.quizsystem.dto.stats.AdminStatsResponse;
import com.quizapp.quizsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(statisticsService.getStats());
    }
}
