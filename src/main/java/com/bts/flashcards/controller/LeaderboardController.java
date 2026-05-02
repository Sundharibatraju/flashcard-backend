package com.bts.flashcards.controller;

import com.bts.flashcards.dto.LeaderboardDTO;
import com.bts.flashcards.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    // GET /api/leaderboard/global
    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardDTO>> getGlobal() {
        return ResponseEntity.ok(
                leaderboardService.getGlobalLeaderboard());
    }

    // GET /api/leaderboard/weekly
    @GetMapping("/weekly")
    public ResponseEntity<List<LeaderboardDTO>> getWeekly() {
        return ResponseEntity.ok(
                leaderboardService.getWeeklyLeaderboard());
    }

    // GET /api/leaderboard/friends
    @GetMapping("/friends")
    public ResponseEntity<List<LeaderboardDTO>> getFriends() {
        return ResponseEntity.ok(
                leaderboardService.getFriendsLeaderboard());
    }
}