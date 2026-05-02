package com.bts.flashcards.repository;

import com.bts.flashcards.model.CardProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardProgressRepository extends JpaRepository<CardProgress, Long> {

    Optional<CardProgress> findByUserIdAndFlashcardId(Long userId, Long flashcardId);

    // Get all due cards for a user in a deck
    @Query("""
        SELECT cp FROM CardProgress cp
        JOIN Flashcard f ON f.id = cp.flashcardId
        WHERE cp.userId = :userId
        AND f.deck.id = :deckId
        AND cp.dueDate <= :today
        ORDER BY cp.dueDate ASC
    """)
    List<CardProgress> findDueCards(
            @Param("userId") Long userId,
            @Param("deckId") Long deckId,
            @Param("today") LocalDate today
    );

    // Count due cards for reminder email
    @Query("""
        SELECT COUNT(cp) FROM CardProgress cp
        WHERE cp.userId = :userId
        AND cp.dueDate <= :today
    """)
    Long countDueCards(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );

    // Delete progress when card is deleted
    void deleteByFlashcardId(Long flashcardId);
}