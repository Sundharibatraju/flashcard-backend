package com.bts.flashcards.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_progress",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "flashcard_id"}
        ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "flashcard_id", nullable = false)
    private Long flashcardId;

    @Column(name = "easiness_factor")
    private Double easinessFactor = 2.5;

    @Column(name = "interval_days")
    private Integer intervalDays = 1;

    @Column(name = "repetitions")
    private Integer repetitions = 0;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "last_reviewed")
    private LocalDateTime lastReviewed;

    @PrePersist
    public void prePersist() {
        dueDate = LocalDate.now();
    }
}