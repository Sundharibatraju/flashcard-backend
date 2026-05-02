package com.bts.flashcards.controller;

import com.bts.flashcards.dto.DeckDTO;
import com.bts.flashcards.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    // GET /api/decks — my decks
    @GetMapping
    public ResponseEntity<List<DeckDTO>> getMyDecks() {
        return ResponseEntity.ok(deckService.getMyDecks());
    }

    // GET /api/decks/{id} — single deck
    @GetMapping("/{id}")
    public ResponseEntity<DeckDTO> getDeckById(@PathVariable Long id) {
        return ResponseEntity.ok(deckService.getDeckById(id));
    }

    // GET /api/decks/public — all public decks
    @GetMapping("/public")
    public ResponseEntity<List<DeckDTO>> getPublicDecks(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(
                    deckService.searchPublicDecks(search));
        }
        return ResponseEntity.ok(deckService.getPublicDecks());
    }

    // POST /api/decks — create deck
    @PostMapping
    public ResponseEntity<DeckDTO> createDeck(
            @Valid @RequestBody DeckDTO dto) {
        return ResponseEntity.ok(deckService.createDeck(dto));
    }

    // PUT /api/decks/{id} — update deck
    @PutMapping("/{id}")
    public ResponseEntity<DeckDTO> updateDeck(
            @PathVariable Long id,
            @Valid @RequestBody DeckDTO dto) {
        return ResponseEntity.ok(deckService.updateDeck(id, dto));
    }

    // DELETE /api/decks/{id} — delete deck
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.ok("Deck deleted successfully");
    }
}