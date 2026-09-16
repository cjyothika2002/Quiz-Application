package com.quizapp.quizsystem.dto.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Section 11: what a user sees for one option while taking a quiz.
 * There is deliberately no `correct` field on this class at all — not
 * hidden via @JsonIgnore, simply absent — so there is no code path that
 * could ever leak it into an active-attempt response.
 */
@Getter
@AllArgsConstructor
public class OptionAttemptResponse {
    private Long id;
    private String text;
}
