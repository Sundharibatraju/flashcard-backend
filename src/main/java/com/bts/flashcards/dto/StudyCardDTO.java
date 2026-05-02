package com.bts.flashcards.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyCardDTO {

    private Long id;
    private String frontText;
    private String backText;
    private String exampleSentence;
    private Double easinessFactor;
    private Integer intervalDays;
    private Integer repetitions;
    private LocalDate dueDate;
}