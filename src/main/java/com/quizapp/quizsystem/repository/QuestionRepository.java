package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizId(Long quizId);

    /**
     * Used at publish-time to enforce Section 8: a quiz cannot be published
     * unless its question bank has at least `questionsPerAttempt` questions.
     * Also used whenever a question is deleted, to re-check the same rule
     * for an already-published quiz.
     */
    long countByQuizId(Long quizId);
}
