package com.bts.flashcards.service;

import com.bts.flashcards.dto.FriendDTO;
import com.bts.flashcards.model.*;
import com.bts.flashcards.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final UserPointsRepository pointsRepository;

    // Get current logged in user
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get all my friends
    public List<FriendDTO> getMyFriends() {
        User currentUser = getCurrentUser();

        List<Friendship> friendships = friendshipRepository
                .findAllFriendships(currentUser.getId());

        return friendships.stream().map(f -> {
                    Long friendId = f.getUserId()
                            .equals(currentUser.getId())
                            ? f.getFriendId() : f.getUserId();

                    User friend = userRepository
                            .findById(friendId).orElse(null);

                    if (friend == null) return null;

                    Integer totalPoints = pointsRepository
                            .findByUserId(friend.getId())
                            .map(UserPoints::getTotalPoints)
                            .orElse(0);

                    return FriendDTO.builder()
                            .userId(friend.getId())
                            .username(friend.getUsername())
                            .avatarUrl(friend.getAvatarUrl())
                            .streakCount(friend.getStreakCount())
                            .totalPoints(totalPoints)
                            .status("ACCEPTED")
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Get pending friend requests
    public List<FriendDTO> getPendingRequests() {
        User currentUser = getCurrentUser();

        return friendshipRepository
                .findPendingRequests(currentUser.getId())
                .stream().map(f -> {
                    User sender = userRepository
                            .findById(f.getUserId()).orElse(null);
                    if (sender == null) return null;

                    return FriendDTO.builder()
                            .userId(sender.getId())
                            .username(sender.getUsername())
                            .avatarUrl(sender.getAvatarUrl())
                            .streakCount(sender.getStreakCount())
                            .status("PENDING")
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Send friend request
    public String sendFriendRequest(Long friendId) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(friendId)) {
            throw new RuntimeException(
                    "Cannot send request to yourself");
        }

        // Check if already friends
        Optional<Friendship> existing = friendshipRepository
                .findByUserIdAndFriendId(
                        currentUser.getId(), friendId);

        if (existing.isPresent()) {
            throw new RuntimeException(
                    "Friend request already sent");
        }

        User friend = userRepository.findById(friendId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Friendship friendship = Friendship.builder()
                .userId(currentUser.getId())
                .friendId(friendId)
                .status("PENDING")
                .build();

        friendshipRepository.save(friendship);
        return "Friend request sent to " + friend.getUsername();
    }

    // Accept friend request
    public String acceptFriendRequest(Long userId) {
        User currentUser = getCurrentUser();

        Friendship friendship = friendshipRepository
                .findByUserIdAndFriendId(userId, currentUser.getId())
                .orElseThrow(() ->
                        new RuntimeException("Friend request not found"));

        friendship.setStatus("ACCEPTED");
        friendshipRepository.save(friendship);

        User sender = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return "You are now friends with " + sender.getUsername();
    }

    // Remove friend
    public String removeFriend(Long friendId) {
        User currentUser = getCurrentUser();

        Friendship friendship = friendshipRepository
                .findByUserIdAndFriendId(
                        currentUser.getId(), friendId)
                .orElseGet(() -> friendshipRepository
                        .findByUserIdAndFriendId(
                                friendId, currentUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Friendship not found")));

        friendshipRepository.delete(friendship);
        return "Friend removed successfully";
    }

    // Search users by username
    public List<FriendDTO> searchUsers(String username) {
        User currentUser = getCurrentUser();

        return userRepository.findAll()
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId())
                        && u.getUsername().toLowerCase()
                        .contains(username.toLowerCase()))
                .map(u -> FriendDTO.builder()
                        .userId(u.getId())
                        .username(u.getUsername())
                        .avatarUrl(u.getAvatarUrl())
                        .streakCount(u.getStreakCount())
                        .build())
                .collect(Collectors.toList());
    }
}