package com.quizapp.quizsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Section 20's "robust attempt-state strategy" rather than relying on
 * `beforeunload`: every 30 seconds, sweep the DB for ACTIVE attempts whose
 * expiryTime has already passed and finalize them as ABANDONED. This
 * guarantees no attempt is left ACTIVE forever just because the user closed
 * the browser without the frontend ever calling submit.
 */
@Component
@RequiredArgsConstructor
public class AttemptExpiryScheduler {

    private final AttemptService attemptService;

    @Scheduled(fixedRate = 30_000)
    public void sweep() {
        attemptService.sweepExpiredAttempts();
    }
}
