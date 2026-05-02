package com.bts.flashcards.repository;

import com.bts.flashcards.model.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {

    Optional<UserPoints> findByUserId(Long userId);

    // Global leaderboard top 100
    List<UserPoints> findTop100ByOrderByTotalPointsDesc();

    // Weekly leaderboard top 100
    List<UserPoints> findTop100ByOrderByWeeklyPointsDesc();
}