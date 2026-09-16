package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

    /**
     * Used by the admin-side question service to enforce "exactly one
     * correct option per question" (Section 10) whenever a question is
     * created or edited.
     */
    long countByQuestionIdAndCorrectTrue(Long questionId);
}
