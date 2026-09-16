package com.quizapp.quizsystem.controller;

import com.quizapp.quizsystem.dto.quiz.QuizRequest;
import com.quizapp.quizsystem.dto.quiz.QuizResponse;
import com.quizapp.quizsystem.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
public class AdminQuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizResponse> create(@Valid @RequestBody QuizRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<QuizResponse>> findAll() {
        return ResponseEntity.ok(quizService.findAllForAdmin());
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> update(@PathVariable Long id, @Valid @RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.update(id, request));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<QuizResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.publish(id));
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<QuizResponse> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.unpublish(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
