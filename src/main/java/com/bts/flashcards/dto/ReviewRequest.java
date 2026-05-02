package com.bts.flashcards.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotNull(message = "Quality is required")
    @Min(value = 0, message = "Quality minimum is 0")
    @Max(value = 5, message = "Quality maximum is 5")
    private Integer quality;

    private Long deckId;
    private Integer durationSeconds;
}