package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.attempt.*;
import com.quizapp.quizsystem.entity.*;
import com.quizapp.quizsystem.exception.AttemptStateException;
import com.quizapp.quizsystem.exception.BadRequestException;
import com.quizapp.quizsystem.exception.ResourceNotFoundException;
import com.quizapp.quizsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final QuizService quizService;
    private final UserService userService;

    /** Section 13: TIME_PER_QUESTION as one configurable constant. */
    @Value("${app.quiz.seconds-per-question}")
    private int secondsPerQuestion;

    // ---------------------------------------------------------------
    // STARTING / RESUMING AN ATTEMPT
    // ---------------------------------------------------------------

    /**
     * Section 17 end-to-end, plus Section 43 (refresh-safe). If the user
     * already has an ACTIVE attempt for this quiz, that attempt is resumed
     * as-is rather than drawing a brand-new random question set.
     */
    @Transactional
    public AttemptStartResponse startOrResume(Long userId, Long quizId) {
        Optional<Attempt> existingActive =
                attemptRepository.findByUserIdAndQuizIdAndStatus(userId, quizId, AttemptStatus.ACTIVE);

        if (existingActive.isPresent()) {
            Attempt attempt = existingActive.get();
            // The attempt is stale (browser was closed past expiry and the
            // user is only now coming back) — finalize it as ABANDONED and
            // fall through to start a fresh attempt instead of resuming a dead one.
            if (LocalDateTime.now().isAfter(attempt.getExpiryTime())) {
                finalizeAttempt(attempt, AttemptStatus.ABANDONED);
            } else {
                return buildStartResponse(attempt);
            }
        }

        return createNewAttempt(userId, quizId);
    }

    private AttemptStartResponse createNewAttempt(Long userId, Long quizId) {
        User user = userService.findById(userId);
        Quiz quiz = quizService.getQuizOrThrow(quizId);

        if (!quiz.isPublished()) {
            throw new BadRequestException("This quiz is not currently available");
        }

        List<Question> bank = questionRepository.findByQuizId(quizId);
        // Defensive re-check even though publishing already enforced this
        // (Section 8/34: never assume an earlier validation still holds —
        // a question may have been deleted after this quiz was published).
        if (bank.size() < quiz.getQuestionsPerAttempt()) {
            throw new BadRequestException("Quiz does not have enough questions to start an attempt");
        }

        List<Question> selected = pickRandom(bank, quiz.getQuestionsPerAttempt());

        LocalDateTime start = LocalDateTime.now();
        int totalQuestions = selected.size();
        LocalDateTime expiry = start.plusSeconds((long) totalQuestions * secondsPerQuestion);

        Attempt attempt = Attempt.builder()
                .user(user)
                .quiz(quiz)
                .status(AttemptStatus.ACTIVE)
                .startTime(start)
                .expiryTime(expiry)
                .totalQuestions(totalQuestions)
                .correctCount(0)
                .wrongCount(0)
                .unansweredCount(0)
                .score(0)
                .build();
        attempt = attemptRepository.save(attempt);

        // Section 9/10: random question order AND random option order,
        // frozen per-attempt right here so a refresh reloads the same set.
        int position = 0;
        for (Question question : selected) {
            List<Long> optionIds = question.getOptions().stream().map(Option::getId).collect(Collectors.toList());
            Collections.shuffle(optionIds);
            String optionOrder = optionIds.stream().map(String::valueOf).collect(Collectors.joining(","));

            AttemptQuestion aq = AttemptQuestion.builder()
                    .attempt(attempt)
                    .question(question)
                    .optionOrder(optionOrder)
                    .position(position++)
                    .build();
            attemptQuestionRepository.save(aq);
        }

        return buildStartResponse(attempt);
    }

    private List<Question> pickRandom(List<Question> bank, int count) {
        List<Question> copy = new ArrayList<>(bank);
        Collections.shuffle(copy);
        return copy.subList(0, count);
    }

    private AttemptStartResponse buildStartResponse(Attempt attempt) {
        List<AttemptQuestion> attemptQuestions =
                attemptQuestionRepository.findByAttemptIdOrderByPosition(attempt.getId());

        List<QuestionAttemptResponse> questionDtos = attemptQuestions.stream()
                .map(this::toQuestionAttemptResponse)
                .toList();

        long secondsRemaining = Math.max(0, Duration.between(LocalDateTime.now(), attempt.getExpiryTime()).getSeconds());

        return new AttemptStartResponse(
                attempt.getId(), attempt.getQuiz().getTitle(), attempt.getTotalQuestions(),
                attempt.getStartTime(), attempt.getExpiryTime(), secondsRemaining, questionDtos);
    }

    private QuestionAttemptResponse toQuestionAttemptResponse(AttemptQuestion aq) {
        Question question = aq.getQuestion();
        Map<Long, Option> optionsById = question.getOptions().stream()
                .collect(Collectors.toMap(Option::getId, o -> o));

        List<OptionAttemptResponse> orderedOptions = Arrays.stream(aq.getOptionOrder().split(","))
                .map(Long::valueOf)
                .map(optionsById::get)
                .filter(Objects::nonNull)
                .map(o -> new OptionAttemptResponse(o.getId(), o.getText()))
                .toList();

        return new QuestionAttemptResponse(question.getId(), aq.getPosition(), question.getText(), orderedOptions);
    }

    // ---------------------------------------------------------------
    // ANSWERING
    // ---------------------------------------------------------------

    /**
     * Section 18: validates ownership, that the attempt is still ACTIVE and
     * not expired, that the question actually belongs to this attempt, and
     * that the selected option actually belongs to that question — never
     * trusting IDs from the client at face value.
     */
    @Transactional
    public void saveAnswer(Long userId, Long attemptId, AnswerRequest request) {
        Attempt attempt = getOwnedAttempt(userId, attemptId);

        if (attempt.getStatus() != AttemptStatus.ACTIVE) {
            throw new AttemptStateException("This attempt is no longer active");
        }
        if (LocalDateTime.now().isAfter(attempt.getExpiryTime())) {
            throw new AttemptStateException("This attempt has expired and can no longer be modified");
        }

        AttemptQuestion aq = attemptQuestionRepository
                .findByAttemptIdAndQuestionId(attemptId, request.getQuestionId())
                .orElseThrow(() -> new BadRequestException("This question does not belong to this attempt"));

        Option selectedOption = null;
        if (request.getSelectedOptionId() != null) {
            selectedOption = optionRepository.findById(request.getSelectedOptionId())
                    .orElseThrow(() -> new BadRequestException("Invalid option"));
            if (!selectedOption.getQuestion().getId().equals(aq.getQuestion().getId())) {
                throw new BadRequestException("Selected option does not belong to this question");
            }
        }

        AttemptAnswer answer = attemptAnswerRepository
                .findByAttemptIdAndQuestionId(attemptId, request.getQuestionId())
                .orElse(AttemptAnswer.builder().attempt(attempt).question(aq.getQuestion()).build());

        answer.setSelectedOption(selectedOption);
        answer.setAnsweredAt(LocalDateTime.now());
        attemptAnswerRepository.save(answer);
    }

    // ---------------------------------------------------------------
    // SUBMISSION (MANUAL OR AUTO, ON TIMEOUT)
    // ---------------------------------------------------------------

    /**
     * Section 21/22. Called either by the user clicking Submit, or by the
     * frontend automatically when its timer reaches zero (Section 15) — both
     * paths land here and produce a COMPLETED attempt. Whatever answers
     * exist at this moment are what gets scored; there is no further chance
     * to answer once this runs (Section 37: reject duplicate submissions).
     */
    @Transactional
    public AttemptResultResponse submit(Long userId, Long attemptId) {
        Attempt attempt = getOwnedAttempt(userId, attemptId);

        if (attempt.getStatus() != AttemptStatus.ACTIVE) {
            throw new AttemptStateException("This attempt has already been finalized");
        }

        finalizeAttempt(attempt, AttemptStatus.COMPLETED);
        return toResultResponse(attempt);
    }

    /**
     * Shared scoring + finalization logic for both explicit submission
     * (COMPLETED) and the background expiry sweep discovering a
     * never-submitted attempt (ABANDONED) — Section 19/20/21/22.
     */
    private void finalizeAttempt(Attempt attempt, AttemptStatus finalStatus) {
        List<AttemptAnswer> answers = attemptAnswerRepository.findByAttemptId(attempt.getId());

        int correct = 0;
        int wrong = 0;
        for (AttemptAnswer answer : answers) {
            if (answer.getSelectedOption() == null) {
                continue; // counted as unanswered below
            }
            if (answer.getSelectedOption().isCorrect()) {
                correct++;
            } else {
                wrong++;
            }
        }
        int answered = answers.size();
        int unanswered = attempt.getTotalQuestions() - answered;
        // Section 12: no negative marking — score is simply the correct count.
        int score = correct;

        attempt.setCorrectCount(correct);
        attempt.setWrongCount(wrong);
        attempt.setUnansweredCount(Math.max(unanswered, 0));
        attempt.setScore(score);
        attempt.setStatus(finalStatus);
        attempt.setSubmittedAt(LocalDateTime.now());

        attemptRepository.save(attempt);
    }

    private AttemptResultResponse toResultResponse(Attempt attempt) {
        int answered = attempt.getCorrectCount() + attempt.getWrongCount();
        double percentage = attempt.getTotalQuestions() == 0
                ? 0
                : (attempt.getScore() * 100.0) / attempt.getTotalQuestions();
        long timeTakenSeconds = Duration.between(attempt.getStartTime(),
                attempt.getSubmittedAt() != null ? attempt.getSubmittedAt() : LocalDateTime.now()).getSeconds();

        return new AttemptResultResponse(
                attempt.getId(), attempt.getQuiz().getTitle(), attempt.getTotalQuestions(),
                answered, attempt.getCorrectCount(), attempt.getWrongCount(), attempt.getUnansweredCount(),
                attempt.getScore(), percentage, timeTakenSeconds, attempt.getStatus());
    }

    // ---------------------------------------------------------------
    // RESULT / REVIEW / HISTORY
    // ---------------------------------------------------------------

    public AttemptResultResponse getResult(Long userId, Long attemptId) {
        Attempt attempt = getOwnedAttempt(userId, attemptId);
        if (attempt.getStatus() == AttemptStatus.ACTIVE) {
            throw new BadRequestException("This attempt has not been submitted yet");
        }
        return toResultResponse(attempt);
    }

    /** Section 24: correct answers are only ever revealed through this explicit endpoint. */
    public List<AnswerReviewResponse> getReview(Long userId, Long attemptId) {
        Attempt attempt = getOwnedAttempt(userId, attemptId);
        if (attempt.getStatus() == AttemptStatus.ACTIVE) {
            throw new BadRequestException("This attempt has not been submitted yet");
        }

        List<AttemptQuestion> attemptQuestions =
                attemptQuestionRepository.findByAttemptIdOrderByPosition(attemptId);
        Map<Long, AttemptAnswer> answersByQuestionId = attemptAnswerRepository.findByAttemptId(attemptId).stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        List<AnswerReviewResponse> review = new ArrayList<>();
        for (AttemptQuestion aq : attemptQuestions) {
            Question question = aq.getQuestion();
            Option correctOption = question.getOptions().stream()
                    .filter(Option::isCorrect).findFirst().orElse(null);
            AttemptAnswer answer = answersByQuestionId.get(question.getId());
            Option selected = answer != null ? answer.getSelectedOption() : null;

            review.add(new AnswerReviewResponse(
                    question.getId(),
                    question.getText(),
                    selected != null ? selected.getText() : null,
                    correctOption != null ? correctOption.getText() : null,
                    selected != null && correctOption != null && selected.getId().equals(correctOption.getId()),
                    question.getExplanation()));
        }
        return review;
    }

    /** Section 25: preserves every attempt, most recent first. */
    public List<AttemptHistoryResponse> getHistory(Long userId) {
        return attemptRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(a -> new AttemptHistoryResponse(
                        a.getId(), a.getQuiz().getTitle(), a.getScore(), a.getTotalQuestions(),
                        a.getStatus(), a.getStartTime(), a.getSubmittedAt()))
                .toList();
    }

    /** Section 27: simple progress/improvement stats derived from stored attempts. */
    public UserProgressResponse getProgress(Long userId) {
        List<Attempt> all = attemptRepository.findByUserIdOrderByStartTimeDesc(userId);
        long completed = all.stream().filter(a -> a.getStatus() == AttemptStatus.COMPLETED).count();
        long abandoned = all.stream().filter(a -> a.getStatus() == AttemptStatus.ABANDONED).count();

        List<Attempt> finalized = all.stream()
                .filter(a -> a.getStatus() != AttemptStatus.ACTIVE)
                .toList();

        double best = finalized.stream().mapToDouble(this::percentageOf).max().orElse(0);
        double avg = finalized.stream().mapToDouble(this::percentageOf).average().orElse(0);
        double latest = finalized.isEmpty() ? 0 : percentageOf(finalized.get(0));

        return new UserProgressResponse(all.size(), completed, abandoned, best, avg, latest);
    }

    private double percentageOf(Attempt a) {
        return a.getTotalQuestions() == 0 ? 0 : (a.getScore() * 100.0) / a.getTotalQuestions();
    }

    // ---------------------------------------------------------------
    // EXPIRY SWEEP (called by a scheduled job — see AttemptExpiryScheduler)
    // ---------------------------------------------------------------

    /**
     * Section 19/20: catches attempts the client never explicitly submitted
     * (browser closed, tab crashed, `beforeunload` didn't fire) and
     * finalizes them as ABANDONED using whatever answers were saved before
     * the user left. Never deletes anything.
     */
    @Transactional
    public int sweepExpiredAttempts() {
        List<Attempt> stale = attemptRepository.findByStatusAndExpiryTimeBefore(
                AttemptStatus.ACTIVE, LocalDateTime.now());
        stale.forEach(a -> finalizeAttempt(a, AttemptStatus.ABANDONED));
        return stale.size();
    }

    // ---------------------------------------------------------------

    private Attempt getOwnedAttempt(Long userId, Long attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
        if (!attempt.getUser().getId().equals(userId)) {
            throw new BadRequestException("This attempt does not belong to you");
        }
        return attempt;
    }
}
