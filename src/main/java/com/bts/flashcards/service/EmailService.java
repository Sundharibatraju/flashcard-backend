package com.bts.flashcards.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReminderEmail(
            String toEmail,
            String username,
            long dueCount) {
        try {
            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject(
                    "🧠 You have " + dueCount
                            + " flashcards due today!");

            message.setText(
                    "Hi " + username + "!\n\n" +
                            "You have " + dueCount +
                            " flashcard(s) due for review today.\n\n" +
                            "Keep your streak going! 🔥\n\n" +
                            "Open the app and start studying:\n" +
                            "👉 http://localhost:3000\n\n" +
                            "Happy learning!\n" +
                            "— Flashcard App Team"
            );

            mailSender.send(message);
            log.info("Reminder email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}",
                    toEmail, e.getMessage());
        }
    }

    public void sendWelcomeEmail(
            String toEmail, String username) {
        try {
            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject(
                    "🎉 Welcome to Flashcard App, "
                            + username + "!");

            message.setText(
                    "Hi " + username + "!\n\n" +
                            "Welcome to Flashcard App! 🎓\n\n" +
                            "Here's how to get started:\n" +
                            "1. Create your first deck\n" +
                            "2. Add flashcards\n" +
                            "3. Start studying!\n\n" +
                            "Our spaced repetition algorithm will\n" +
                            "help you remember everything! 🧠\n\n" +
                            "👉 http://localhost:3000\n\n" +
                            "Happy learning!\n" +
                            "— Flashcard App Team"
            );

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}",
                    toEmail, e.getMessage());
        }
    }
}