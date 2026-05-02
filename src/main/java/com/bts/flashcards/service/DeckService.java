package com.bts.flashcards.service;

import com.bts.flashcards.dto.DeckDTO;
import com.bts.flashcards.model.Deck;
import com.bts.flashcards.model.User;
import com.bts.flashcards.repository.DeckRepository;
import com.bts.flashcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    // Get current logged in user
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get all my decks
    public List<DeckDTO> getMyDecks() {
        User user = getCurrentUser();
        return deckRepository.findByUserId(user.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get single deck
    public DeckDTO getDeckById(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));
        return toDTO(deck);
    }

    // Get all public decks
    public List<DeckDTO> getPublicDecks() {
        return deckRepository.findByIsPublicTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Search public decks
    public List<DeckDTO> searchPublicDecks(String keyword) {
        return deckRepository
                .findByIsPublicTrueAndTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Create deck
    public DeckDTO createDeck(DeckDTO dto) {
        User user = getCurrentUser();

        Deck deck = Deck.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .languageFrom(dto.getLanguageFrom())
                .languageTo(dto.getLanguageTo())
                .isPublic(dto.getIsPublic() != null
                        ? dto.getIsPublic() : false)
                .cardCount(0)
                .build();

        return toDTO(deckRepository.save(deck));
    }

    // Update deck
    public DeckDTO updateDeck(Long id, DeckDTO dto) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // Make sure owner is updating
        User user = getCurrentUser();
        if (!deck.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        deck.setTitle(dto.getTitle());
        deck.setDescription(dto.getDescription());
        deck.setLanguageFrom(dto.getLanguageFrom());
        deck.setLanguageTo(dto.getLanguageTo());
        deck.setIsPublic(dto.getIsPublic());

        return toDTO(deckRepository.save(deck));
    }

    // Delete deck
    public void deleteDeck(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // Make sure owner is deleting
        User user = getCurrentUser();
        if (!deck.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        deckRepository.delete(deck);
    }

    // Convert Deck to DeckDTO
    public DeckDTO toDTO(Deck deck) {
        return DeckDTO.builder()
                .id(deck.getId())
                .title(deck.getTitle())
                .description(deck.getDescription())
                .languageFrom(deck.getLanguageFrom())
                .languageTo(deck.getLanguageTo())
                .isPublic(deck.getIsPublic())
                .cardCount(deck.getCardCount())
                .build();
    }
}