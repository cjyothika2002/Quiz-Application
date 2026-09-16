package com.quizapp.quizsystem.controller;

import com.quizapp.quizsystem.dto.question.QuestionAdminResponse;
import com.quizapp.quizsystem.dto.question.QuestionRequest;
import com.quizapp.quizsystem.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<QuestionAdminResponse> create(@PathVariable Long quizId,
                                                         @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(quizId, request));
    }

    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<List<QuestionAdminResponse>> findByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.findByQuiz(quizId));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionAdminResponse> update(@PathVariable Long questionId,
                                                         @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(questionService.update(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> delete(@PathVariable Long questionId) {
        questionService.delete(questionId);
        return ResponseEntity.noContent().build();
    }
}
