package com.bts.flashcards.repository;

import com.bts.flashcards.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    // Get all decks by user
    List<Deck> findByUserId(Long userId);

    // Get all public decks
    List<Deck> findByIsPublicTrue();

    // Search public decks by title
    List<Deck> findByIsPublicTrueAndTitleContainingIgnoreCase(String title);
}