package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.AttemptQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, Long> {

    /**
     * Returns the frozen question set for an attempt, in display order.
     * This is what a page refresh re-reads instead of drawing a new random
     * sample (Section 17/43).
     */
    List<AttemptQuestion> findByAttemptIdOrderByPosition(Long attemptId);

    /**
     * Used when saving an answer, to verify the question actually belongs
     * to this attempt (Section 18: "never trust IDs blindly from the
     * frontend") before an AttemptAnswer row is written or updated.
     */
    Optional<AttemptQuestion> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
}
