package com.bts.flashcards.service;

import com.bts.flashcards.model.User;
import com.bts.flashcards.repository.CardProgressRepository;
import com.bts.flashcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final UserRepository userRepository;
    private final CardProgressRepository progressRepository;
    private final EmailService emailService;

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReminders() {
        log.info("Running daily reminder scheduler...");

        List<User> users = userRepository.findAll();
        int emailsSent = 0;

        for (User user : users) {
            try {
                long dueCount = progressRepository
                        .countDueCards(
                                user.getId(), LocalDate.now());

                if (dueCount > 0) {
                    emailService.sendReminderEmail(
                            user.getEmail(),
                            user.getUsername(),
                            dueCount
                    );
                    emailsSent++;
                }
            } catch (Exception e) {
                log.error("Error processing user {}: {}",
                        user.getEmail(), e.getMessage());
            }
        }

        log.info("Daily reminders sent: {}", emailsSent);
    }

    // Reset weekly points every Monday at midnight
    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyPoints() {
        log.info("Resetting weekly points...");
        // Will implement with UserPointsRepository
    }
}