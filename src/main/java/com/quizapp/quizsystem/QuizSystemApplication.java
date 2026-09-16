package com.quizapp.quizsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @EnableScheduling activates AttemptExpiryScheduler's @Scheduled sweep,
 * which is how abandoned attempts (Section 19/20) get finalized even when
 * the client never calls anything.
 */
@SpringBootApplication
@EnableScheduling
public class QuizSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizSystemApplication.class, args);
    }
}
