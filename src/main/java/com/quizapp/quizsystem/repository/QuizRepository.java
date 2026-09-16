package com.quizapp.quizsystem.repository;

import com.quizapp.quizsystem.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /** Quiz browsing for users must only ever see published quizzes (Section 5/11). */
    List<Quiz> findByPublishedTrue();

    List<Quiz> findByCategoryId(Long categoryId);

    List<Quiz> findByCategoryIdAndPublishedTrue(Long categoryId);
}
