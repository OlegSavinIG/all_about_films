package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@ContextConfiguration(classes = {FriendshipsDbStorage.class})
class FriendshipsDbStorageTest {

    @Test
    void addFriend() {
    }

    @Test
    void deleteFriend() {
    }

    @Test
    void getAllSameFriends() {
    }

    @Test
    void getAllFriends() {
    }
}