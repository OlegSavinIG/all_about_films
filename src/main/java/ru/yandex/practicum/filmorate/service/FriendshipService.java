package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.FriendshipsStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Service
public class FriendshipService {
    private final FriendshipsStorage friendshipsStorage;

    @Autowired
    public FriendshipService(FriendshipsStorage friendshipsStorage) {
        this.friendshipsStorage = friendshipsStorage;
    }


    public void addFriend(long firstUserId, long secondUserId) {
        friendshipsStorage.addFriend(firstUserId, secondUserId);
    }

    public void deleteFriend(long firstUserId, long secondUserId) {
        friendshipsStorage.deleteFriend(firstUserId, secondUserId);
    }

    public Set<User> getAllSameFriends(long firstUserId, long secondUserId) {
        return friendshipsStorage.getAllSameFriends(firstUserId, secondUserId);
    }

    public Set<User> getAllFriends(long id) {
        Set<User> allFriends = friendshipsStorage.getAllFriends(id);
        return allFriends;
    }
}
