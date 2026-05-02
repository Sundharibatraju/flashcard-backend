package com.bts.flashcards.repository;

import com.bts.flashcards.model.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByUserIdOrderByStudiedAtDesc(Long userId);

    List<StudySession> findByUserIdAndDeckId(Long userId, Long deckId);

    // Total cards studied by user
    @Query("""
        SELECT SUM(s.cardsStudied) FROM StudySession s
        WHERE s.userId = :userId
    """)
    Long totalCardsStudied(@Param("userId") Long userId);

    // Accuracy rate for a deck
    @Query("""
        SELECT SUM(s.cardsCorrect) * 100.0 / SUM(s.cardsStudied)
        FROM StudySession s
        WHERE s.userId = :userId
        AND s.deckId = :deckId
    """)
    Double accuracyRate(
            @Param("userId") Long userId,
            @Param("deckId") Long deckId
    );
}