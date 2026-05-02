package com.bts.flashcards.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer totalPoints;
    private Integer weeklyPoints;
    private Integer cardsMastered;
    private Integer streakCount;
    private Integer rank;
}