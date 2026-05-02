package com.bts.flashcards.service;

import com.bts.flashcards.model.CardProgress;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SM2Service {

    // quality: 0-5
    // 0 = complete blackout
    // 1 = incorrect, remembered after seeing answer
    // 2 = incorrect, easy to recall
    // 3 = correct, but difficult
    // 4 = correct, after hesitation
    // 5 = perfect response

    public CardProgress calculate(CardProgress progress, int quality) {

        if (quality < 0 || quality > 5) {
            throw new IllegalArgumentException(
                    "Quality must be between 0 and 5");
        }

        // If answer was wrong (quality < 3) reset progress
        if (quality < 3) {
            progress.setRepetitions(0);
            progress.setIntervalDays(1);
        } else {
            // Calculate next interval
            if (progress.getRepetitions() == 0) {
                progress.setIntervalDays(1);
            } else if (progress.getRepetitions() == 1) {
                progress.setIntervalDays(6);
            } else {
                int newInterval = (int) Math.round(
                        progress.getIntervalDays()
                                * progress.getEasinessFactor()
                );
                progress.setIntervalDays(newInterval);
            }
            progress.setRepetitions(progress.getRepetitions() + 1);
        }

        // Update easiness factor (EF)
        double ef = progress.getEasinessFactor()
                + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));

        // EF should never go below 1.3
        progress.setEasinessFactor(Math.max(1.3, ef));

        // Set next due date
        progress.setDueDate(
                LocalDate.now().plusDays(progress.getIntervalDays()));
        progress.setLastReviewed(LocalDateTime.now());

        return progress;
    }
}