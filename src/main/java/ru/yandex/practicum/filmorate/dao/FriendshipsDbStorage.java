package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.storage.FriendshipsStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FriendshipsDbStorage implements FriendshipsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;
    private final String sqlAdd = "INSERT into friendships (user_id, friend_id) values (?, ?)";

    private final String sqlDelete = "DELETE from friendships  WHERE user_id = ? AND friend_id = ?";

    private final String sqlGetSame = "SELECT f1.friend_id " +
            "FROM friendships f1 " +
            "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    private final String sqlAllFriends = "SELECT friend_id from friendships where user_id = ?";


    @Autowired
    public FriendshipsDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }


    @Override
    public void addFriend(long firstUserId, long secondUserId) {
        if (isUserExists(firstUserId) && isUserExists(secondUserId)) {
            log.info("Добавление дружбы между пользователями {} и {}", firstUserId, secondUserId);
            jdbcTemplate.update(sqlAdd, firstUserId, secondUserId);
        }
    }

    @Override
    public void deleteFriend(long firstUserId, long secondUserId) {
        if (isUserExists(firstUserId) && isUserExists(secondUserId)) {
            log.info("Удаление дружбы между пользователями {} и {}", firstUserId, secondUserId);
            jdbcTemplate.update(sqlDelete, firstUserId, secondUserId);
        }
    }

    @Override
    public Set<User> getAllSameFriends(long id, long otherId) {
        log.info("Поиск общих друзей между пользователями {} и {}", id, otherId);
        List<Long> friendIds = jdbcTemplate.query(sqlGetSame, new Object[]{id, otherId}, (rs, rowNum) -> rs.getLong("friend_id"));
        Set<User> commonFriends = friendIds.stream()
                .map(i -> userStorage.getById(i).get())
                .collect(Collectors.toSet());
        return commonFriends;
    }

    @Override
    public Set<User> getAllFriends(long id) {
        if (isUserExists(id)) {
            log.info("Поиск друзей пользователя {}", id);
            List<Long> friendsId = jdbcTemplate.query(sqlAllFriends, (rs, rowNum) -> rs.getLong("friend_id"), id);
            if (friendsId.isEmpty()) {
                log.info("Нет друзей у пользователя {}", id);
                return new HashSet<>();
            }
            Set<User> friends = friendsId.stream()
                    .map(i -> userStorage.getById(i).orElse(null))
                    .collect(Collectors.toSet());
            return friends;
        }
        log.warn("Пользователь {} не найден", id);
        return Collections.emptySet();
    }

    private boolean isUserExists(long userId) {
        return userStorage.getById(userId).isPresent();
    }

}
