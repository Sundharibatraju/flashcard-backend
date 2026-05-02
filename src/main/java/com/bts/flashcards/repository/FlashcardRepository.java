package com.bts.flashcards.repository;

import com.bts.flashcards.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    // Get all cards in a deck
    List<Flashcard> findByDeckId(Long deckId);

    // Count cards in a deck
    Integer countByDeckId(Long deckId);

    // Delete all cards in a deck
    void deleteByDeckId(Long deckId);
}