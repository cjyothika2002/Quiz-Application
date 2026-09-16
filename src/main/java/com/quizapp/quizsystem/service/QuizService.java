package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.quiz.QuizBrowseResponse;
import com.quizapp.quizsystem.dto.quiz.QuizRequest;
import com.quizapp.quizsystem.dto.quiz.QuizResponse;
import com.quizapp.quizsystem.entity.Category;
import com.quizapp.quizsystem.entity.Quiz;
import com.quizapp.quizsystem.exception.BadRequestException;
import com.quizapp.quizsystem.exception.ResourceNotFoundException;
import com.quizapp.quizsystem.repository.CategoryRepository;
import com.quizapp.quizsystem.repository.QuestionRepository;
import com.quizapp.quizsystem.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;

    /** TIME_PER_QUESTION from Section 13 — one configurable constant, read from application.yml. */
    @Value("${app.quiz.seconds-per-question}")
    private int secondsPerQuestion;

    @Transactional
    public QuizResponse create(QuizRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .questionsPerAttempt(request.getQuestionsPerAttempt())
                .published(false) // a new quiz always starts unpublished
                .build();

        return toAdminResponse(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse update(Long id, QuizRequest request) {
        Quiz quiz = getQuizOrThrow(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setCategory(category);
        quiz.setQuestionsPerAttempt(request.getQuestionsPerAttempt());

        // If shrinking questionsPerAttempt or changing category invalidates
        // publish-readiness, un-publish rather than leave a broken published
        // quiz live for users (defends the Section 8 invariant on every edit,
        // not just at the moment of publishing).
        if (quiz.isPublished() && !hasEnoughQuestions(quiz)) {
            quiz.setPublished(false);
        }

        return toAdminResponse(quizRepository.save(quiz));
    }

    /**
     * Enforces Section 8: a quiz cannot go live unless its question bank
     * already has at least `questionsPerAttempt` questions. This is checked
     * here, server-side, regardless of what the admin UI may have already
     * validated client-side (Section 34: never assume frontend validation is enough).
     */
    @Transactional
    public QuizResponse publish(Long id) {
        Quiz quiz = getQuizOrThrow(id);
        if (!hasEnoughQuestions(quiz)) {
            long available = questionRepository.countByQuizId(id);
            throw new BadRequestException(
                    "Cannot publish: quiz requires " + quiz.getQuestionsPerAttempt()
                            + " questions per attempt but only " + available + " exist in the question bank");
        }
        quiz.setPublished(true);
        return toAdminResponse(quizRepository.save(quiz));
    }

    @Transactional
    public QuizResponse unpublish(Long id) {
        Quiz quiz = getQuizOrThrow(id);
        quiz.setPublished(false);
        return toAdminResponse(quizRepository.save(quiz));
    }

    @Transactional
    public void delete(Long id) {
        Quiz quiz = getQuizOrThrow(id);
        quizRepository.delete(quiz);
    }

    public List<QuizResponse> findAllForAdmin() {
        return quizRepository.findAll().stream().map(this::toAdminResponse).toList();
    }

    /** USER-facing browsing (Section 5) — published quizzes only, no bank-size, no answers. */
    public List<QuizBrowseResponse> findPublishedForUsers() {
        return quizRepository.findByPublishedTrue().stream().map(this::toBrowseResponse).toList();
    }

    public Quiz getQuizOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

    private boolean hasEnoughQuestions(Quiz quiz) {
        return questionRepository.countByQuizId(quiz.getId()) >= quiz.getQuestionsPerAttempt();
    }

    private QuizResponse toAdminResponse(Quiz quiz) {
        long bankSize = questionRepository.countByQuizId(quiz.getId());
        return new QuizResponse(
                quiz.getId(), quiz.getTitle(), quiz.getDescription(),
                quiz.getCategory().getId(), quiz.getCategory().getName(),
                quiz.getQuestionsPerAttempt(), bankSize, quiz.isPublished());
    }

    private QuizBrowseResponse toBrowseResponse(Quiz quiz) {
        // Section 13: time limit is always computed server-side, never entered by the admin.
        int timeLimitMinutes = (quiz.getQuestionsPerAttempt() * secondsPerQuestion) / 60;
        return new QuizBrowseResponse(
                quiz.getId(), quiz.getTitle(), quiz.getDescription(),
                quiz.getCategory().getName(), quiz.getQuestionsPerAttempt(), timeLimitMinutes);
    }
}
