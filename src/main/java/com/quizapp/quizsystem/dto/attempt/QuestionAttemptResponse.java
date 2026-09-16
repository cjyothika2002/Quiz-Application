package com.quizapp.quizsystem.dto.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionAttemptResponse {
    private Long questionId;
    private int position;
    private String text;
    private List<OptionAttemptResponse> options;
}
