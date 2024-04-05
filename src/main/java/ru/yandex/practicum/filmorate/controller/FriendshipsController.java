package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;

import java.util.Set;

@RestController
public class FriendshipsController {
    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipsController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<Set<User>> findFriends(@PathVariable Long id) {
        Set<User> friends = friendshipService.getAllFriends(id);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Set<User> commonFriends = friendshipService.getAllSameFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        friendshipService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        friendshipService.deleteFriend(id, friendId);
        return ResponseEntity.ok().build();
    }
}
