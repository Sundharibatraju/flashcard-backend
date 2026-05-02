package com.bts.flashcards.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title max 100 characters")
    private String title;

    private String description;

    private String languageFrom;

    private String languageTo;

    private Boolean isPublic = false;

    private Integer cardCount;
}