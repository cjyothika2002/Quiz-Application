package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {

    List<AttemptAnswer> findByAttemptId(Long attemptId);

    /**
     * Used to upsert: if the user already answered this question in this
     * attempt and changes their mind before submitting, we update the
     * existing row rather than inserting a duplicate (the DB-level unique
     * constraint on (attempt_id, question_id) backs this up, Section 18/44).
     */
    Optional<AttemptAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);

    long countByAttemptId(Long attemptId);
}
