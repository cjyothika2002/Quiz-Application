package com.quizapp.quizsystem.dto.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String text;

    private String explanation;

    @NotEmpty(message = "At least 2 options are required")
    @Valid
    private List<OptionRequest> options;
}
