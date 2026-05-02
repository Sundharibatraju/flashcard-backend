package com.bts.flashcards.service;

import com.bts.flashcards.dto.StudyCardDTO;
import com.bts.flashcards.dto.ReviewRequest;
import com.bts.flashcards.model.*;
import com.bts.flashcards.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final CardProgressRepository progressRepository;
    private final FlashcardRepository flashcardRepository;
    private final StudySessionRepository sessionRepository;
    private final UserPointsRepository pointsRepository;
    private final UserRepository userRepository;
    private final SM2Service sm2Service;

    // Get current logged in user
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get due cards for today
    public List<StudyCardDTO> getDueCards(Long deckId) {
        User user = getCurrentUser();

        // Get all cards in deck
        List<Flashcard> allCards =
                flashcardRepository.findByDeckId(deckId);

        if (allCards.isEmpty()) {
            return new ArrayList<>();
        }

        // Initialize progress for new cards
        for (Flashcard card : allCards) {
            boolean exists = progressRepository
                    .findByUserIdAndFlashcardId(user.getId(), card.getId())
                    .isPresent();

            if (!exists) {
                CardProgress newProgress = CardProgress.builder()
                        .userId(user.getId())
                        .flashcardId(card.getId())
                        .easinessFactor(2.5)
                        .intervalDays(1)
                        .repetitions(0)
                        .dueDate(LocalDate.now())
                        .build();
                progressRepository.save(newProgress);
            }
        }

        // Get due cards
        List<CardProgress> dueProgress = progressRepository
                .findDueCards(user.getId(), deckId, LocalDate.now());

        // Map to StudyCardDTO
        return dueProgress.stream()
                .map(progress -> {
                    Flashcard card = flashcardRepository
                            .findById(progress.getFlashcardId())
                            .orElse(null);
                    if (card == null) return null;

                    return StudyCardDTO.builder()
                            .id(card.getId())
                            .frontText(card.getFrontText())
                            .backText(card.getBackText())
                            .exampleSentence(card.getExampleSentence())
                            .easinessFactor(progress.getEasinessFactor())
                            .intervalDays(progress.getIntervalDays())
                            .repetitions(progress.getRepetitions())
                            .dueDate(progress.getDueDate())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Submit review result
    @Transactional
    public Map<String, Object> submitReview(ReviewRequest request) {
        User user = getCurrentUser();

        // Get or create progress
        CardProgress progress = progressRepository
                .findByUserIdAndFlashcardId(
                        user.getId(), request.getCardId())
                .orElseGet(() -> {
                    CardProgress newProgress = CardProgress.builder()
                            .userId(user.getId())
                            .flashcardId(request.getCardId())
                            .easinessFactor(2.5)
                            .intervalDays(1)
                            .repetitions(0)
                            .dueDate(LocalDate.now())
                            .build();
                    return progressRepository.save(newProgress);
                });

        // Apply SM-2 algorithm
        sm2Service.calculate(progress, request.getQuality());
        progressRepository.save(progress);

        // Award points if correct
        int pointsEarned = 0;
        if (request.getQuality() >= 3) {
            pointsEarned = request.getQuality() * 10;
            awardPoints(user.getId(), pointsEarned);
        }

        // Update streak
        updateStreak(user);

        // Save session if deckId provided
        if (request.getDeckId() != null) {
            saveSession(user.getId(), request);
        }

        // Return result
        Map<String, Object> result = new HashMap<>();
        result.put("nextDueDate", progress.getDueDate());
        result.put("intervalDays", progress.getIntervalDays());
        result.put("easinessFactor", progress.getEasinessFactor());
        result.put("pointsEarned", pointsEarned);
        result.put("correct", request.getQuality() >= 3);
        return result;
    }

    // Get study stats for a deck
    public Map<String, Object> getDeckStats(Long deckId) {
        User user = getCurrentUser();

        int totalCards = flashcardRepository
                .countByDeckId(deckId);
        long dueToday = progressRepository
                .countDueCards(user.getId(), LocalDate.now());
        Double accuracy = sessionRepository
                .accuracyRate(user.getId(), deckId);
        Long totalStudied = sessionRepository
                .totalCardsStudied(user.getId());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCards", totalCards);
        stats.put("dueToday", dueToday);
        stats.put("accuracy", accuracy != null
                ? Math.round(accuracy) : 0);
        stats.put("totalStudied",
                totalStudied != null ? totalStudied : 0);
        stats.put("streakCount", user.getStreakCount());
        return stats;
    }

    // Award points to user
    private void awardPoints(Long userId, int points) {
        UserPoints userPoints = pointsRepository
                .findByUserId(userId)
                .orElse(new UserPoints(userId));

        userPoints.setTotalPoints(
                userPoints.getTotalPoints() + points);
        userPoints.setWeeklyPoints(
                userPoints.getWeeklyPoints() + points);
        pointsRepository.save(userPoints);
    }

    // Update streak
    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastPractice = user.getLastPracticeDate();

        if (lastPractice == null
                || lastPractice.isBefore(today)) {
            if (lastPractice != null
                    && lastPractice.equals(today.minusDays(1))) {
                // Practiced yesterday - increase streak
                user.setStreakCount(user.getStreakCount() + 1);
            } else if (lastPractice == null
                    || lastPractice.isBefore(today.minusDays(1))) {
                // Missed a day - reset streak
                user.setStreakCount(1);
            }
            user.setLastPracticeDate(today);
            userRepository.save(user);
        }
    }

    // Save study session
    private void saveSession(Long userId, ReviewRequest request) {
        StudySession session = StudySession.builder()
                .userId(userId)
                .deckId(request.getDeckId())
                .cardsStudied(1)
                .cardsCorrect(request.getQuality() >= 3 ? 1 : 0)
                .durationSeconds(request.getDurationSeconds() != null
                        ? request.getDurationSeconds() : 0)
                .build();
        sessionRepository.save(session);
    }
}