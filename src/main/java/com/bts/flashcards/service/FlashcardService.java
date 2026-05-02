package com.bts.flashcards.service;

import com.bts.flashcards.dto.FlashcardDTO;
import com.bts.flashcards.model.Deck;
import com.bts.flashcards.model.Flashcard;
import com.bts.flashcards.repository.DeckRepository;
import com.bts.flashcards.repository.FlashcardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;

    // Get all cards in a deck
    public List<FlashcardDTO> getCardsByDeck(Long deckId) {
        return flashcardRepository.findByDeckId(deckId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get single card
    public FlashcardDTO getCardById(Long id) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Flashcard not found"));
        return toDTO(card);
    }

    // Create single card
    public FlashcardDTO createCard(Long deckId, FlashcardDTO dto) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() ->
                        new RuntimeException("Deck not found"));

        Flashcard card = Flashcard.builder()
                .deck(deck)
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .exampleSentence(dto.getExampleSentence())
                .imageUrl(dto.getImageUrl())
                .build();

        flashcardRepository.save(card);

        // Update card count in deck
        deck.setCardCount(deck.getCardCount() + 1);
        deckRepository.save(deck);

        return toDTO(card);
    }

    // Create multiple cards at once
    public List<FlashcardDTO> createMultipleCards(
            Long deckId, List<FlashcardDTO> dtos) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() ->
                        new RuntimeException("Deck not found"));

        List<Flashcard> cards = dtos.stream()
                .map(dto -> Flashcard.builder()
                        .deck(deck)
                        .frontText(dto.getFrontText())
                        .backText(dto.getBackText())
                        .exampleSentence(dto.getExampleSentence())
                        .imageUrl(dto.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        flashcardRepository.saveAll(cards);

        // Update card count
        deck.setCardCount(deck.getCardCount() + cards.size());
        deckRepository.save(deck);

        return cards.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Update card
    public FlashcardDTO updateCard(Long id, FlashcardDTO dto) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Flashcard not found"));

        card.setFrontText(dto.getFrontText());
        card.setBackText(dto.getBackText());
        card.setExampleSentence(dto.getExampleSentence());
        card.setImageUrl(dto.getImageUrl());

        return toDTO(flashcardRepository.save(card));
    }

    // Delete single card
    public void deleteCard(Long id) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Flashcard not found"));

        // Update deck card count
        Deck deck = card.getDeck();
        deck.setCardCount(Math.max(0, deck.getCardCount() - 1));
        deckRepository.save(deck);

        flashcardRepository.delete(card);
    }

    // Delete all cards in deck
    public void deleteAllCards(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() ->
                        new RuntimeException("Deck not found"));

        flashcardRepository.deleteByDeckId(deckId);

        deck.setCardCount(0);
        deckRepository.save(deck);
    }

    // Convert to DTO
    public FlashcardDTO toDTO(Flashcard card) {
        return FlashcardDTO.builder()
                .id(card.getId())
                .frontText(card.getFrontText())
                .backText(card.getBackText())
                .exampleSentence(card.getExampleSentence())
                .imageUrl(card.getImageUrl())
                .build();
    }
}