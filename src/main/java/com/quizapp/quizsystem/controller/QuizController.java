package com.quizapp.quizsystem.controller;

import com.quizapp.quizsystem.dto.quiz.QuizBrowseResponse;
import com.quizapp.quizsystem.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Section 5: any authenticated user can browse published quizzes. */
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<List<QuizBrowseResponse>> browse() {
        return ResponseEntity.ok(quizService.findPublishedForUsers());
    }
}
