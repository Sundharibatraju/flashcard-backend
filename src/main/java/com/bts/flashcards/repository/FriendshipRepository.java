package com.bts.flashcards.repository;

import com.bts.flashcards.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository
        extends JpaRepository<Friendship, Long> {

    // Find friendship between two users
    Optional<Friendship> findByUserIdAndFriendId(
            Long userId, Long friendId);

    // Find all accepted friends
    @Query("""
        SELECT f.friendId FROM Friendship f
        WHERE f.userId = :userId
        AND f.status = 'ACCEPTED'
        UNION
        SELECT f.userId FROM Friendship f
        WHERE f.friendId = :userId
        AND f.status = 'ACCEPTED'
    """)
    List<Long> findAcceptedFriendIds(@Param("userId") Long userId);

    // Find all pending requests received
    @Query("""
        SELECT f FROM Friendship f
        WHERE f.friendId = :userId
        AND f.status = 'PENDING'
    """)
    List<Friendship> findPendingRequests(@Param("userId") Long userId);

    // Find all accepted friendships
    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.userId = :userId OR f.friendId = :userId)
        AND f.status = 'ACCEPTED'
    """)
    List<Friendship> findAllFriendships(@Param("userId") Long userId);
}