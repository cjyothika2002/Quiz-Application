package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.stats.AdminStatsResponse;
import com.quizapp.quizsystem.entity.Attempt;
import com.quizapp.quizsystem.entity.AttemptStatus;
import com.quizapp.quizsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalCategories = categoryRepository.count();
        long totalQuizzes = quizRepository.count();
        long totalQuestions = questionRepository.count();
        long totalAttempts = attemptRepository.count();
        long completed = attemptRepository.countByStatus(AttemptStatus.COMPLETED);
        long abandoned = attemptRepository.countByStatus(AttemptStatus.ABANDONED);

        List<Attempt> completedAttempts = attemptRepository.findAll().stream()
                .filter(a -> a.getStatus() == AttemptStatus.COMPLETED)
                .toList();
        double avgScorePercentage = completedAttempts.stream()
                .mapToDouble(a -> a.getTotalQuestions() == 0 ? 0 : (a.getScore() * 100.0) / a.getTotalQuestions())
                .average()
                .orElse(0);

        return new AdminStatsResponse(
                totalUsers, totalCategories, totalQuizzes, totalQuestions,
                totalAttempts, completed, abandoned, avgScorePercentage);
    }
}
