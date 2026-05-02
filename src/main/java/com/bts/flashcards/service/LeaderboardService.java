package com.bts.flashcards.service;

import com.bts.flashcards.dto.LeaderboardDTO;
import com.bts.flashcards.model.*;
import com.bts.flashcards.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserPointsRepository pointsRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    // Get current logged in user
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Global leaderboard top 100
    public List<LeaderboardDTO> getGlobalLeaderboard() {
        List<UserPoints> topPoints = pointsRepository
                .findTop100ByOrderByTotalPointsDesc();

        return mapToLeaderboard(topPoints);
    }

    // Weekly leaderboard top 100
    public List<LeaderboardDTO> getWeeklyLeaderboard() {
        List<UserPoints> topPoints = pointsRepository
                .findTop100ByOrderByWeeklyPointsDesc();

        return mapToLeaderboard(topPoints);
    }

    // Friends leaderboard
    public List<LeaderboardDTO> getFriendsLeaderboard() {
        User currentUser = getCurrentUser();

        // Get all accepted friends
        List<Long> friendIds = friendshipRepository
                .findAcceptedFriendIds(currentUser.getId());

        // Include current user
        friendIds.add(currentUser.getId());

        // Get points for all friends
        List<UserPoints> friendPoints = pointsRepository
                .findAll()
                .stream()
                .filter(p -> friendIds.contains(p.getUserId()))
                .sorted((a, b) -> b.getTotalPoints()
                        .compareTo(a.getTotalPoints()))
                .collect(Collectors.toList());

        return mapToLeaderboard(friendPoints);
    }

    // Map UserPoints to LeaderboardDTO
    private List<LeaderboardDTO> mapToLeaderboard(
            List<UserPoints> pointsList) {
        List<LeaderboardDTO> result = new ArrayList<>();

        for (int i = 0; i < pointsList.size(); i++) {
            UserPoints points = pointsList.get(i);
            User user = userRepository
                    .findById(points.getUserId())
                    .orElse(null);

            if (user != null) {
                result.add(LeaderboardDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .avatarUrl(user.getAvatarUrl())
                        .totalPoints(points.getTotalPoints())
                        .weeklyPoints(points.getWeeklyPoints())
                        .cardsMastered(points.getCardsMastered())
                        .streakCount(user.getStreakCount())
                        .rank(i + 1)
                        .build());
            }
        }
        return result;
    }
}