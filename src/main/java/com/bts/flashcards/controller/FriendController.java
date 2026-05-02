package com.bts.flashcards.controller;

import com.bts.flashcards.dto.FriendDTO;
import com.bts.flashcards.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // GET /api/friends
    @GetMapping
    public ResponseEntity<List<FriendDTO>> getMyFriends() {
        return ResponseEntity.ok(friendService.getMyFriends());
    }

    // GET /api/friends/pending
    @GetMapping("/pending")
    public ResponseEntity<List<FriendDTO>> getPending() {
        return ResponseEntity.ok(
                friendService.getPendingRequests());
    }

    // GET /api/friends/search?username=john
    @GetMapping("/search")
    public ResponseEntity<List<FriendDTO>> searchUsers(
            @RequestParam String username) {
        return ResponseEntity.ok(
                friendService.searchUsers(username));
    }

    // POST /api/friends/request/{friendId}
    @PostMapping("/request/{friendId}")
    public ResponseEntity<String> sendRequest(
            @PathVariable Long friendId) {
        return ResponseEntity.ok(
                friendService.sendFriendRequest(friendId));
    }

    // POST /api/friends/accept/{userId}
    @PostMapping("/accept/{userId}")
    public ResponseEntity<String> acceptRequest(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                friendService.acceptFriendRequest(userId));
    }

    // DELETE /api/friends/{friendId}
    @DeleteMapping("/{friendId}")
    public ResponseEntity<String> removeFriend(
            @PathVariable Long friendId) {
        return ResponseEntity.ok(
                friendService.removeFriend(friendId));
    }
}