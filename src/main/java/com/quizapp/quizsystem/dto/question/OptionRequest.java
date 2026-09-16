package com.quizapp.quizsystem.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    private boolean correct;
}
