package com.bts.flashcards.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "weekly_points")
    private Integer weeklyPoints = 0;

    @Column(name = "cards_mastered")
    private Integer cardsMastered = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserPoints(Long userId) {
        this.userId = userId;
        this.totalPoints = 0;
        this.weeklyPoints = 0;
        this.cardsMastered = 0;
    }

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}