package com.bts.flashcards.controller;

import com.bts.flashcards.dto.*;
import com.bts.flashcards.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    // GET /api/study/{deckId}/due
    // Get all due cards for today
    @GetMapping("/{deckId}/due")
    public ResponseEntity<List<StudyCardDTO>> getDueCards(
            @PathVariable Long deckId) {
        return ResponseEntity.ok(
                studyService.getDueCards(deckId));
    }

    // POST /api/study/review
    // Submit a card review
    @PostMapping("/review")
    public ResponseEntity<Map<String, Object>> submitReview(
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(
                studyService.submitReview(request));
    }

    // GET /api/study/{deckId}/stats
    // Get study statistics for a deck
    @GetMapping("/{deckId}/stats")
    public ResponseEntity<Map<String, Object>> getDeckStats(
            @PathVariable Long deckId) {
        return ResponseEntity.ok(
                studyService.getDeckStats(deckId));
    }
}