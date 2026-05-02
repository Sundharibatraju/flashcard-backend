package com.bts.flashcards.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardDTO {

    private Long id;

    @NotBlank(message = "Front text is required")
    @Size(max = 500)
    private String frontText;

    @NotBlank(message = "Back text is required")
    @Size(max = 500)
    private String backText;

    private String exampleSentence;

    private String imageUrl;
}