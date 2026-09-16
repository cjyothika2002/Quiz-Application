package com.quizapp.quizsystem.controller;

import com.quizapp.quizsystem.dto.attempt.*;
import com.quizapp.quizsystem.security.UserPrincipal;
import com.quizapp.quizsystem.service.AttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    /** Section 17/43: starts a new attempt, or resumes the existing ACTIVE one for this quiz. */
    @PostMapping("/api/quizzes/{quizId}/attempts")
    public ResponseEntity<AttemptStartResponse> startOrResume(@PathVariable Long quizId,
                                                               @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attemptService.startOrResume(principal.getId(), quizId));
    }

    /** Section 18: save/change one answer within an active attempt. */
    @PostMapping("/api/attempts/{attemptId}/answers")
    public ResponseEntity<Void> saveAnswer(@PathVariable Long attemptId,
                                            @Valid @RequestBody AnswerRequest request,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        attemptService.saveAnswer(principal.getId(), attemptId, request);
        return ResponseEntity.ok().build();
    }

    /** Section 21: manual submit, or called by the frontend automatically on timeout (Section 15). */
    @PostMapping("/api/attempts/{attemptId}/submit")
    public ResponseEntity<AttemptResultResponse> submit(@PathVariable Long attemptId,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attemptService.submit(principal.getId(), attemptId));
    }

    @GetMapping("/api/attempts/{attemptId}/result")
    public ResponseEntity<AttemptResultResponse> getResult(@PathVariable Long attemptId,
                                                            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attemptService.getResult(principal.getId(), attemptId));
    }

    /** Section 24: correct answers only ever revealed via this explicit endpoint. */
    @GetMapping("/api/attempts/{attemptId}/review")
    public ResponseEntity<List<AnswerReviewResponse>> getReview(@PathVariable Long attemptId,
                                                                 @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attemptService.getReview(principal.getId(), attemptId));
    }

    @GetMapping("/api/attempts/history")
    public ResponseEntity<List<AttemptHistoryResponse>> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attemptService.getHistory(principal.getId()));
    }

    @GetMapping("/api/attempts/progress")
    public ResponseEntity<UserProgressResponse> getProgress(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attemptService.getProgress(principal.getId()));
    }
}
