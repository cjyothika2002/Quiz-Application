package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.leaderboard.LeaderboardEntry;
import com.quizapp.quizsystem.entity.Attempt;
import com.quizapp.quizsystem.entity.AttemptStatus;
import com.quizapp.quizsystem.entity.User;
import com.quizapp.quizsystem.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final AttemptRepository attemptRepository;

    /**
     * Section 26: only COMPLETED attempts participate (the repository query
     * itself excludes ABANDONED — see AttemptRepository.findByQuizIdAndStatus).
     * Each user contributes only their single best attempt, tie-broken by
     * less time taken.
     */
    public List<LeaderboardEntry> getLeaderboard(Long quizId) {
        List<Attempt> completed = attemptRepository.findByQuizIdAndStatus(quizId, AttemptStatus.COMPLETED);

        Map<Long, Attempt> bestPerUser = completed.stream()
                .collect(Collectors.toMap(
                        a -> a.getUser().getId(),
                        a -> a,
                        this::pickBetter));

        List<Attempt> ranked = bestPerUser.values().stream()
                .sorted(Comparator
                        .comparingInt(Attempt::getScore).reversed()
                        .thenComparingLong(this::timeTakenSeconds))
                .toList();

        List<LeaderboardEntry> entries = new java.util.ArrayList<>();
        int rank = 1;
        for (Attempt a : ranked) {
            User user = a.getUser();
            entries.add(new LeaderboardEntry(
                    rank++, user.getName(), a.getScore(), a.getTotalQuestions(), timeTakenSeconds(a)));
        }
        return entries;
    }

    /** Higher score wins; if tied, less time taken wins (Section 26). */
    private Attempt pickBetter(Attempt a, Attempt b) {
        if (a.getScore() != b.getScore()) {
            return a.getScore() > b.getScore() ? a : b;
        }
        return timeTakenSeconds(a) <= timeTakenSeconds(b) ? a : b;
    }

    private long timeTakenSeconds(Attempt a) {
        if (a.getSubmittedAt() == null) return Long.MAX_VALUE;
        return Duration.between(a.getStartTime(), a.getSubmittedAt()).getSeconds();
    }
}
