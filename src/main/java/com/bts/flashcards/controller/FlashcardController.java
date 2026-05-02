package com.bts.flashcards.controller;

import com.bts.flashcards.dto.FlashcardDTO;
import com.bts.flashcards.service.FlashcardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    // GET /api/decks/{deckId}/cards — all cards in deck
    @GetMapping("/decks/{deckId}/cards")
    public ResponseEntity<List<FlashcardDTO>> getCards(
            @PathVariable Long deckId) {
        return ResponseEntity.ok(
                flashcardService.getCardsByDeck(deckId));
    }

    // GET /api/cards/{id} — single card
    @GetMapping("/cards/{id}")
    public ResponseEntity<FlashcardDTO> getCard(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                flashcardService.getCardById(id));
    }

    // POST /api/decks/{deckId}/cards — create single card
    @PostMapping("/decks/{deckId}/cards")
    public ResponseEntity<FlashcardDTO> createCard(
            @PathVariable Long deckId,
            @Valid @RequestBody FlashcardDTO dto) {
        return ResponseEntity.ok(
                flashcardService.createCard(deckId, dto));
    }

    // POST /api/decks/{deckId}/cards/batch — create multiple cards
    @PostMapping("/decks/{deckId}/cards/batch")
    public ResponseEntity<List<FlashcardDTO>> createMultipleCards(
            @PathVariable Long deckId,
            @RequestBody List<FlashcardDTO> dtos) {
        return ResponseEntity.ok(
                flashcardService.createMultipleCards(deckId, dtos));
    }

    // PUT /api/cards/{id} — update card
    @PutMapping("/cards/{id}")
    public ResponseEntity<FlashcardDTO> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody FlashcardDTO dto) {
        return ResponseEntity.ok(
                flashcardService.updateCard(id, dto));
    }

    // DELETE /api/cards/{id} — delete single card
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<String> deleteCard(
            @PathVariable Long id) {
        flashcardService.deleteCard(id);
        return ResponseEntity.ok("Card deleted successfully");
    }

    // DELETE /api/decks/{deckId}/cards — delete all cards
    @DeleteMapping("/decks/{deckId}/cards")
    public ResponseEntity<String> deleteAllCards(
            @PathVariable Long deckId) {
        flashcardService.deleteAllCards(deckId);
        return ResponseEntity.ok("All cards deleted successfully");
    }
}