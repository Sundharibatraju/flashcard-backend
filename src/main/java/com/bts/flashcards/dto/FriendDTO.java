package com.bts.flashcards.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer streakCount;
    private Integer totalPoints;
    private String status; // PENDING, ACCEPTED
}