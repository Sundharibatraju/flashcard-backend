package com.bts.flashcards.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "deck_id", nullable = false)
    private Long deckId;

    @Column(name = "cards_studied")
    private Integer cardsStudied = 0;

    @Column(name = "cards_correct")
    private Integer cardsCorrect = 0;

    @Column(name = "duration_seconds")
    private Integer durationSeconds = 0;

    @Column(name = "studied_at")
    private LocalDateTime studiedAt;

    @PrePersist
    public void prePersist() {
        studiedAt = LocalDateTime.now();
    }
}