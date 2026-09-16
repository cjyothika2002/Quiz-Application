package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.question.OptionRequest;
import com.quizapp.quizsystem.dto.question.QuestionAdminResponse;
import com.quizapp.quizsystem.dto.question.QuestionRequest;
import com.quizapp.quizsystem.entity.Option;
import com.quizapp.quizsystem.entity.Question;
import com.quizapp.quizsystem.entity.Quiz;
import com.quizapp.quizsystem.exception.BadRequestException;
import com.quizapp.quizsystem.exception.ResourceNotFoundException;
import com.quizapp.quizsystem.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizService quizService;

    @Transactional
    public QuestionAdminResponse create(Long quizId, QuestionRequest request) {
        Quiz quiz = quizService.getQuizOrThrow(quizId);
        validateExactlyOneCorrectOption(request);

        Question question = Question.builder()
                .quiz(quiz)
                .text(request.getText())
                .explanation(request.getExplanation())
                .build();

        request.getOptions().forEach(optReq ->
                question.getOptions().add(
                        Option.builder()
                                .question(question)
                                .text(optReq.getText())
                                .correct(optReq.isCorrect())
                                .build()));

        return toAdminResponse(questionRepository.save(question));
    }

    @Transactional
    public QuestionAdminResponse update(Long questionId, QuestionRequest request) {
        Question question = getQuestionOrThrow(questionId);
        validateExactlyOneCorrectOption(request);

        question.setText(request.getText());
        question.setExplanation(request.getExplanation());

        // Replace the option list wholesale rather than trying to diff-patch
        // it — simpler to reason about and to explain, and orphanRemoval on
        // the entity mapping takes care of deleting the old rows.
        question.getOptions().clear();
        request.getOptions().forEach(optReq ->
                question.getOptions().add(
                        Option.builder()
                                .question(question)
                                .text(optReq.getText())
                                .correct(optReq.isCorrect())
                                .build()));

        return toAdminResponse(questionRepository.save(question));
    }

    @Transactional
    public void delete(Long questionId) {
        Question question = getQuestionOrThrow(questionId);
        questionRepository.delete(question);
        // Note: this can drop a published quiz below its questionsPerAttempt
        // requirement. We deliberately don't auto-unpublish here — that
        // decision is enforced the next time the quiz is edited/published
        // via QuizService, and any attempt to START this quiz will also
        // re-validate the count at that point (Section 8/34).
    }

    public List<QuestionAdminResponse> findByQuiz(Long quizId) {
        return questionRepository.findByQuizId(quizId).stream().map(this::toAdminResponse).toList();
    }

    public Question getQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
    }

    /**
     * Section 10: "mark exactly one correct option for a single-answer MCQ."
     * Enforced here, server-side, on every create/update — never assume the
     * admin UI already guaranteed this (Section 34).
     */
    private void validateExactlyOneCorrectOption(QuestionRequest request) {
        long correctCount = request.getOptions().stream().filter(OptionRequest::isCorrect).count();
        if (correctCount != 1) {
            throw new BadRequestException(
                    "Exactly one option must be marked correct (found " + correctCount + ")");
        }
    }

    private QuestionAdminResponse toAdminResponse(Question question) {
        List<QuestionAdminResponse.OptionAdminResponse> options = question.getOptions().stream()
                .map(o -> new QuestionAdminResponse.OptionAdminResponse(o.getId(), o.getText(), o.isCorrect()))
                .toList();
        return new QuestionAdminResponse(question.getId(), question.getText(), question.getExplanation(), options);
    }
}
