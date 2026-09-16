package com.quizapp.quizsystem.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * ADMIN-ONLY view of a question. This is the one DTO in the whole project
 * that is allowed to carry `correct` flags (via OptionAdminResponse) — it
 * must never be reachable from a USER-role endpoint. The quiz-taking flow
 * uses QuestionAttemptResponse instead, which has no such field (Section 11).
 */
@Getter
@AllArgsConstructor
public class QuestionAdminResponse {
    private Long id;
    private String text;
    private String explanation;
    private List<OptionAdminResponse> options;

    @Getter
    @AllArgsConstructor
    public static class OptionAdminResponse {
        private Long id;
        private String text;
        private boolean correct;
    }
}
